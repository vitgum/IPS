package mobi.eyeline.ips.web.controllers.surveys

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import mobi.eyeline.ips.model.SurveyInvitation
import mobi.eyeline.ips.repository.SurveyInvitationRepository
import mobi.eyeline.ips.repository.SurveyRepository
import mobi.eyeline.ips.service.MadvUpdateService
import mobi.eyeline.ips.service.Services
import mobi.eyeline.ips.web.controllers.BaseController
import mobi.eyeline.util.jsf.components.data_table.model.DataTableModel
import mobi.eyeline.util.jsf.components.data_table.model.DataTableSortOrder
import org.apache.commons.lang3.StringUtils

import javax.faces.bean.ManagedBean

import static mobi.eyeline.ips.model.InvitationUpdateStatus.UNDEFINED
import static mobi.eyeline.ips.web.controllers.TimeZoneHelper.formatDateTime

@CompileStatic
@Slf4j('logger')
@ManagedBean(name = "surveyInvitesController")
class SurveyInvitesController extends BaseController {

  private final SurveyInvitationRepository surveyInvitationRepository =
      Services.instance().surveyInvitationRepository
  private final SurveyRepository surveyRepository = Services.instance().surveyRepository
  private final MadvUpdateService madvUpdateService = Services.instance().madvUpdateService

  // MADV campaign
  String madvId
  boolean madvIdError

  // Invitations
  Date inviteDate = new Date()
  String newInviteDate
  String inviteValue
  boolean inviteError

  @Delegate
  BaseSurveyReadOnlyController surveys =
      beanByName('baseSurveyReadOnlyController', BaseSurveyReadOnlyController)

  SurveyInvitesController() {
    madvId = survey.statistics.campaign

    newInviteDate = formatDateTime(inviteDate, getTimeZone())
  }

  boolean isCampaignDefined() { survey.statistics.campaign != null }

  DataTableModel getTableModel() {
    return new DataTableModel() {
      @Override
      List getRows(int offset,
                   int limit,
                   DataTableSortOrder sortOrder) {

        final List<SurveyInvitation> list = surveyInvitationRepository.list(
            getSurvey(),
            sortOrder.columnId,
            sortOrder.asc,
            limit,
            offset)

        return list.collect {
          new TableItem(
              id: it.id,
              date: it.date,
              value: it.value
          )
        }
      }

      @Override
      int getRowsCount() {
        surveyInvitationRepository.count(getSurvey())
      }
    }
  }

  void addInvite() {
    int inviteValueInt = 0
    try {
      inviteValueInt = Integer.parseInt(inviteValue)
    } catch (Exception ignored) {
    }

    def invite = new SurveyInvitation(
        survey: surveyRepository.load(surveyId),
        date: inviteDate,
        value: inviteValueInt)

    inviteError = renderViolationMessage(validator.validate(invite), [
        'date' : 'inviteDate',
        'value': 'inviteValue'
    ])
    if (!inviteError) {
      surveyInvitationRepository.save(invite)
    }
  }

  void deleteInvite() {
    int id = getParamValue("inviteId").asInteger()
    surveyInvitationRepository.delete(surveyInvitationRepository.load(id))
  }

  void onMadvEditSave() {
    if (StringUtils.isNumeric(madvId)) {
      survey.statistics.with {
        campaign = madvId
        sentCount = 0
        lastUpdate = null
        updateStatus = UNDEFINED
      }
      surveyRepository.update(survey)
      madvUpdateService.runNow(survey.id)

      madvIdError = false

    } else {
      addErrorMessage(
          strings['invitations.block.advertising.company.dialog.id.error'],
          'newIdentifier')
      madvIdError = true
    }
  }

  void onMadvEditCancel() {
    madvId = survey.statistics.campaign
  }

  void clearMadvId() {
    survey.statistics.with {
      campaign = null
      sentCount = 0
      lastUpdate = null
      updateStatus = UNDEFINED
    }
    surveyRepository.update(survey)

    madvId = null
  }

  void updateSentCount() { madvUpdateService.runNow() }

  int getTotalInvitations() {
    surveyInvitationRepository.list(survey).collect { it.value }.sum(0) as int
  }

  static class TableItem implements Serializable {
    int id
    Date date
    int value
  }

}