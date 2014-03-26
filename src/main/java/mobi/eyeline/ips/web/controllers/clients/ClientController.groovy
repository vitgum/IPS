package mobi.eyeline.ips.web.controllers.clients

import groovy.transform.CompileStatic
import mobi.eyeline.ips.model.Locale as IpsLocale
import mobi.eyeline.ips.model.Role
import mobi.eyeline.ips.model.User
import mobi.eyeline.ips.repository.UserRepository
import mobi.eyeline.ips.service.Services
import mobi.eyeline.ips.web.controllers.BaseController

import javax.faces.model.SelectItem

@CompileStatic
class ClientController extends BaseController {

    private final UserRepository userRepository = Services.instance().userRepository

    List<SelectItem> getClients() {
        return userRepository
                .listByRole(Role.CLIENT)
                .findAll { User it -> !it.blocked }
                .collect { User it -> new SelectItem(it.id, it.fullName) }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    List<SelectItem> getLocales() {
        def localeName = { IpsLocale ipsLocale ->
            ResourceBundle
                    .getBundle('ips', ipsLocale.asLocale())
                    .getString('locale.name.select')
        }

        return IpsLocale.values().collect { IpsLocale it -> new SelectItem(it, localeName(it)) }
    }
}
