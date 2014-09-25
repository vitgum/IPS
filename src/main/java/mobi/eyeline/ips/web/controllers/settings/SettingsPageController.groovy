package mobi.eyeline.ips.web.controllers.settings

import groovy.transform.CompileStatic
import mobi.eyeline.ips.model.UiProfile
import mobi.eyeline.ips.model.User
import mobi.eyeline.ips.repository.UserRepository
import mobi.eyeline.ips.service.Services
import mobi.eyeline.ips.web.controllers.BaseController
import mobi.eyeline.ips.web.controllers.LogoBean
import mobi.eyeline.ips.web.validators.ImageValidator
import mobi.eyeline.util.jsf.components.input_file.UploadedFile

import javax.faces.context.FacesContext
import javax.faces.model.SelectItem
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

import static mobi.eyeline.ips.web.controllers.BaseController.getStrings

@SuppressWarnings('UnnecessaryQualifiedReference')
@CompileStatic
class SettingsPageController extends BaseController {

    private final UserRepository userRepository = Services.instance().userRepository
    private final  HttpSession session = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession()
    User user
    UploadedFile imageFile
    Boolean error


    final List<SelectItem> skins = UiProfile.Skin.values().collect {
        UiProfile.Skin skin -> new SelectItem(skin.toString(), nameOf(skin))
    }

    SettingsPageController() {
        user = getCurrentUser()
        session.setAttribute("updatedUser", user)
        if (user.uiProfile == null) {
            user.uiProfile = new UiProfile()
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    String nameOf(UiProfile.Skin skin) {
        //noinspection UnnecessaryQualifiedReference
        strings["settings.skin.$skin".toString()]
    }


    void save() {
        userRepository.update(user)
        LogoBean skinController = (LogoBean)session.getAttribute("logoBean")
        skinController.logo = user.uiProfile.icon
        Services.instance().locationService.skin = user.uiProfile.skin
        error = false
    }

    String cancelSave() {
        session.setAttribute("updatedUser", null)
        return "LOGIN"
    }

    void saveLogo() {
        if (validate()) {
            user.uiProfile.icon = imageFile.inputStream.bytes
        } else {
            addErrorMessage(strings['settings.validation.logo'], 'logo')
        }
    }

    void deleteLogo() {
        user.uiProfile.icon = null
    }

    boolean validate() {
        if (imageFile == null) {
            error = true
            return false
        }
        if(ImageValidator.validate(imageFile.filename)){
            return true
        }
        error = true
        return false
    }
}
