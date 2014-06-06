package mobi.eyeline.ips.service;

import mobi.eyeline.ips.model.Survey;
import mobi.eyeline.ips.repository.InvitationDeliveryRepository;
import mobi.eyeline.ips.repository.SurveyInvitationRepository;
import mobi.eyeline.ips.repository.SurveyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SurveyService {

    private static final Logger logger = LoggerFactory.getLogger(SurveyService.class);

    private final SurveyRepository surveyRepository;
    private final SurveyInvitationRepository surveyInvitationRepository;
    private final InvitationDeliveryRepository invitationDeliveryRepository;

    public SurveyService(SurveyRepository surveyRepository,
                         SurveyInvitationRepository surveyInvitationRepository,
                         InvitationDeliveryRepository invitationDeliveryRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyInvitationRepository = surveyInvitationRepository;
        this.invitationDeliveryRepository = invitationDeliveryRepository;
    }

    /**
     * Loads survey by ID.
     * <br/>
     * Returns {@code null} if at least one of these conditions is not met:
     * <ol>
     *     <li>Survey with this ID exists,</li>
     *     <li>This survey is running now,
     *     i.e. current date is between {@link Survey#startDate} and {@link Survey#endDate}</li>
     *     <li>Survey is not marked as deleted</li>
     * </ol>
     *
     * @param skipValidation if set, the second check is omitted.
     */
    public Survey findSurvey(int surveyId, boolean skipValidation) {
        final Survey survey = surveyRepository.get(surveyId);

        if (survey == null) {
            logger.info("Survey not found for ID = [" + surveyId + "]");
            return null;

        } else if (!skipValidation && !survey.isRunningNow()) {
            logger.info("Survey is not active now, ID = [" + surveyId + "]");
            return null;

        } else if (!survey.isActive()) {
            logger.info("Survey is disabled, ID = [" + surveyId + "]");
            return null;
        }

        return survey;
    }

    /**
     * @return Overall invitations count, including:
     * <ul>
     *     <li>Obtained from MADV,</li>
     *     <li>Actually performed deliveries,</li>
     *     <li>Manually specified,</li>
     * </ul>
     */
    public int countInvitations(Survey survey) {
        return survey.getStatistics().getSentCount() +
                invitationDeliveryRepository.countSent(survey) +
                surveyInvitationRepository.count(survey);
    }
}
