/**
 *    Copyright (c) 2011-2014, OpenIoT
 *
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 */

package org.openiot.ui.request.presentation.web.scopes.controllers.pages;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.sdum.serviceresultset.model.SdumServiceResultSet;
import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.OAuthorizationCredentials;
import org.openiot.ui.request.commons.models.OAMOManager;
import org.openiot.ui.request.commons.providers.SDUMAPIWrapper;
import org.openiot.ui.request.commons.providers.exceptions.APIException;
import org.openiot.ui.request.commons.util.MarshalOSDspecUtils;
import org.openiot.ui.request.presentation.web.model.nodes.interfaces.VisualizationWidget;
import org.openiot.ui.request.presentation.web.scopes.application.ApplicationBean;
import org.openiot.ui.request.presentation.web.scopes.session.SessionBean;
import org.openiot.ui.request.presentation.web.scopes.session.context.pages.RequestPresentationPageContext;
import org.openiot.ui.request.presentation.web.util.FaceletLocalization;
import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.component.panel.Panel;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@ManagedBean(name = "requestPresentationPageController")
@RequestScoped
public class RequestPresentationPageController implements Serializable {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestPresentationPageController.class);
	private static final long serialVersionUID = 1L;

	// Cached context
	private RequestPresentationPageContext cachedContext;
	// Injected properties
	@ManagedProperty(value = "#{applicationBean}")
	protected transient ApplicationBean applicationBean;
	@ManagedProperty(value = "#{sessionBean}")
	protected transient SessionBean sessionBean;
	protected transient ResourceBundle messages;
	private AccessControlUtil acUtil;

	public RequestPresentationPageController() {
		this.messages = FaceletLocalization.getLocalizedResourceBundle();
		acUtil = AccessControlUtil.getInstance();
	}

	public RequestPresentationPageContext getContext() {
		if (cachedContext == null) {
			if (sessionBean.getUserId() == null) {
				OAuthorizationCredentials credentials = acUtil.getOAuthorizationCredentials();
				if(credentials == null)
					return null;
				else
					sessionBean.setUserId(credentials.getUserIdURI());
			}
			cachedContext = (RequestPresentationPageContext) (sessionBean == null ? ApplicationBean.lookupSessionBean() : sessionBean).getContext("requestPresentationPageContext");
			if (cachedContext == null) {
				cachedContext = new RequestPresentationPageContext();
				try {
					LOGGER.info(MarshalOSDspecUtils.marshalOSDSpec(cachedContext.getAppManager().exportOSDSpec()));
				} catch (Exception ex) {
					LOGGER.error("", ex);
				}
			}
		}
		return cachedContext;
	}

	public void doAccessControl() throws IOException {
		if(acUtil.getOAuthorizationCredentials() == null){
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			acUtil.redirectToLogin(req, response);
		}
//		if (sessionBean.getUserId() == null) {
//			applicationBean.redirect("/pages/login.xhtml?faces-redirect=true");
//		}
	}

	// ------------------------------------
	// Controller entrypoints
	// ------------------------------------
	public void keepAlivePing() {
	}

	// ------------------------------------
	// Controllers for application management
	// ------------------------------------
	public void reloadApplications() {
		RequestPresentationPageContext context = getContext();
		if (context.getAppManager() == null) {
			context.setAppManager(new OAMOManager());
		}

		// Load services
		try {
			OAuthorizationCredentials creds = acUtil.getOAuthorizationCredentials();
			context.getAppManager().loadUserOAMOs(ApplicationBean.lookupSessionBean().getUserId(), creds.getClientId(), creds.getAccessToken());
			if (!context.getAppManager().getAvailableOAMOs().isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("GROWL_INFO_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "APPLICATIONS_LOADED_SUCCESSFULLY")));
			}

		} catch (APIException ex) {
			LOGGER.error("", ex);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_REGISTRATION_SERVICE")));
		}

		context.clear();
	}

	public void loadApplication(String name) {
		RequestPresentationPageContext context = getContext();
		context.clear();

		context.getAppManager().selectOAMOByName(name);
		generateDashboardFromOAMO(context.getAppManager().getSelectedOAMO(), 2);
	}

	// ------------------------------------
	// Dashboard management
	// ------------------------------------
	public Dashboard getDashboard() {
		RequestPresentationPageContext context = getContext();

		if (context != null) {
			return context.getDashboard();
		}
		return null;
	}

	public void setDashboard(Dashboard dashboard) {
		RequestPresentationPageContext context = getContext();

		if (context != null) {
			context.setDashboard(dashboard);
		}
	}

	/**
	 * Invoked by the UI to async update a specific dashboard
	 */
	public void updateWidget() {
		Map<String, String> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String serviceId = (String) requestMap.get("serviceId");

		RequestPresentationPageContext context = getContext();
		if (context.getDashboard() == null) {
			return;
		}

		if (serviceId == null || !context.getServiceIdToWidgetMap().containsKey(serviceId)) {
			return;
		}

		// Fetch data
		try {
			OAuthorizationCredentials creds = acUtil.getOAuthorizationCredentials();
			SdumServiceResultSet resultSet = SDUMAPIWrapper.pollForReport(serviceId, creds.getClientId(), creds.getAccessToken());
			context.getServiceIdToWidgetMap().get(serviceId).processData(resultSet);
		} catch (APIException ex) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_SDUM_SERVICE")));
		}
	}

	// ------------------------------------
	// Helpers
	// ------------------------------------

	private void generateDashboardFromOAMO(OAMO oamo, int columnCount) {
		RequestPresentationPageContext context = getContext();
		context.getServiceIdToWidgetMap().clear();

		Dashboard dashboard = context.getDashboard();
		DashboardModel model = new DefaultDashboardModel();
		for (int i = 0, n = columnCount; i < n; i++) {
			DashboardColumn column = new DefaultDashboardColumn();
			model.addColumn(column);
		}
		dashboard.setRendered(true);
		dashboard.setModel(model);

		int nextColumn = 0;
		for (int index = 0; index < oamo.getOSMO().size(); index++) {
			OSMO osmo = oamo.getOSMO().get(index);
			List<PresentationAttr> presentationAttributes = osmo.getRequestPresentation().getWidget().get(0).getPresentationAttr();

			// Discover widget class
			String widgetClass = null;
			for (PresentationAttr attr : presentationAttributes) {
				if ("widgetClass".equals(attr.getName())) {
					widgetClass = attr.getValue().substring(attr.getValue().lastIndexOf('.') + 1);
					break;
				}
			}

			if (widgetClass == null) {
				continue;
			}

			// Try to instanciate widget
			try {
				VisualizationWidget visualizationWidget = (VisualizationWidget) Class.forName("org.openiot.ui.request.presentation.web.model.nodes.impl." + widgetClass).newInstance();
				String serviceId = osmo.getId();
				if (serviceId == null || serviceId.isEmpty()) {
					serviceId = "service_" + System.nanoTime();
				}
				context.getServiceIdToWidgetMap().put(serviceId, visualizationWidget);

				// Instanciate renderer widget
				Panel widgetView = visualizationWidget.createWidget(serviceId, presentationAttributes);

				dashboard.getChildren().add(widgetView);
				DashboardColumn column = dashboard.getModel().getColumn(nextColumn % columnCount);
				column.addWidget(widgetView.getId());

				nextColumn++;

			} catch (InstantiationException e) {
				LOGGER.error("", e);
			} catch (IllegalAccessException e) {
				LOGGER.error("", e);
			} catch (ClassNotFoundException e) {
				LOGGER.error("", e);
			} catch (ClassCastException e) {
				LOGGER.error("", e);
			} catch (Throwable ex) {
				LOGGER.error("", ex);
			}
		}
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

}
