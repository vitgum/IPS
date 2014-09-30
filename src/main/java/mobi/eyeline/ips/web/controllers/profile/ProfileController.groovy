package mobi.eyeline.ips.web.controllers.profile

import groovy.transform.CompileStatic
import mobi.eyeline.ips.model.User
import mobi.eyeline.ips.repository.UserRepository
import mobi.eyeline.ips.service.Services
import mobi.eyeline.ips.service.UserService
import mobi.eyeline.ips.util.HashUtils
import mobi.eyeline.ips.web.controllers.BaseController
import mobi.eyeline.ips.web.controllers.LocaleController
import mobi.eyeline.ips.web.controllers.TimeZoneHelper

import javax.faces.bean.ManagedBean
import javax.faces.model.SelectItem

@CompileStatic
@ManagedBean(name = "profilePageController")
class ProfileController extends BaseController {

    private final UserRepository userRepository = Services.instance().userRepository
    private final UserService userService = Services.instance().userService

    User user

    String currentPassword

    String newPassword
    String newPasswordConfirmation
    LocaleController localeController

    boolean updateOk

    ProfileController() {
        user = getCurrentUser()
        localeController = new LocaleController()
    }

    void saveProfile() {
        updateOk = true
        if (!isPasswordIntact()) {
            // Update user password if corresponding fields are filled in.
            updateOk = updatePassword()
        }

        updateOk &= validateModel()
        if (updateOk) {
            userRepository.update(user)
        }

        localeController.changeLocale(user)
    }

    private boolean isPasswordIntact() {
        currentPassword == null && newPassword == null && newPasswordConfirmation == null
    }

    private boolean updatePassword() {
        if (!userService.checkPassword(user, currentPassword)) {
            addErrorMessage(strings['profile.edit.password.invalid'], 'currentPassword')
            return false
        }

        if (newPassword == null) {
            // XXX: This case somehow passes validation.
            // Why is validator not triggered for empty fields?
            addErrorMessage(strings['profile.edit.message.password.required'], 'newPassword')
            return false
        }

        if (newPassword != newPasswordConfirmation) {
            addErrorMessage(
                    strings['profile.edit.password.confirmation.mismatch'],
                    'newPasswordConfirmation')
            return false
        }

        user.password = HashUtils.hashPassword(newPassword)
        return true
    }

    private boolean validateModel() {
        if (renderViolationMessage(validator.validate(user))) {
            return false
        }

        if (!userService.isEmailAllowed(user)) {
            addErrorMessage(strings['client.dialog.validation.email.exists'], 'email')
            return false
        }

        return true
    }

    List<SelectItem> getTimeZones() { TimeZoneHelper.getTimeZones(getLocale()) }
}
