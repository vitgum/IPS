package mobi.eyeline.ips.web.controllers

import javax.faces.context.FacesContext

import static mobi.eyeline.ips.web.BuildVersion.BUILD_VERSION

class ResourceController implements Serializable {

    /**
     * @return Current request context path.
     */
    String getPath() {
        FacesContext.currentInstance.externalContext.requestContextPath
    }

    String getVersion() { BUILD_VERSION }
}