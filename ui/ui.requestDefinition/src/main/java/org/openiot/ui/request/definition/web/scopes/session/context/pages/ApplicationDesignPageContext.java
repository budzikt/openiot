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

package org.openiot.ui.request.definition.web.scopes.session.context.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.openiot.commons.sensortypes.model.MeasurementCapability;
import org.openiot.commons.sensortypes.model.SensorType;
import org.openiot.commons.sensortypes.model.SensorTypes;
import org.openiot.ui.request.commons.annotations.scanners.GraphNodeScanner;
import org.openiot.ui.request.commons.interfaces.GraphModel;
import org.openiot.ui.request.commons.models.OAMOManager;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.ConnectorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.definition.web.model.nodes.impl.sources.GenericSource;
import org.openiot.ui.request.definition.web.model.validation.GraphValidationError;
import org.openiot.ui.request.definition.web.model.validation.GraphValidationWarning;
import org.openiot.ui.request.definition.web.scopes.session.base.DisposableContext;
import org.primefaces.extensions.model.dynaform.DynaFormModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class ApplicationDesignPageContext extends DisposableContext {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDesignPageContext.class);

	private OAMOManager appManager;
	// The Node graph model
	private GraphModel graphModel;
	// The active property editor form model
	private DynaFormModel propertyEditorModel;
	// The generated (prettyprinted) SPARQL code
	private String generatedCode;
	// The graph validation output (warnings and errors)
	private List<GraphValidationError> graphValidationErrors;
	private List<GraphValidationWarning> graphValidationWarnings;
	// A list of available nodes grouped by type (sensors are always first)
	private Map<String, List<GraphNode>> availableNodesByTypeMap;
	// Selected console tab index
	private int selectedConsoleTabIndex;
	// Selected filters
	private double filterLocationLat;
	private double filterLocationLon;
	private double filterLocationRadius;
	// New application
	private String newApplicationName;
	private String newApplicationDescription;
	// OSD import/export
	private StreamedContent exportedSpec;
	private UploadedFile uploadedSpec;
	private boolean persistSpec;

	public ApplicationDesignPageContext() {
		super();
		this.register();

		graphValidationErrors = new ArrayList<GraphValidationError>();
		graphValidationWarnings = new ArrayList<GraphValidationWarning>();
		availableNodesByTypeMap = new TreeMap<String, List<GraphNode>>();

		// Scan for available nodes
		detectAvailableNodes();
	}

	@Override
	public String getContextUID() {
		return "applicationDesignPageContext";
	}

	public OAMOManager getAppManager() {
		return appManager;
	}

	public void setAppManager(OAMOManager appManager) {
		this.appManager = appManager;
	}


	// --------------------------------------------------------------------------
	// Workspace
	// --------------------------------------------------------------------------

	public GraphModel getGraphModel() {
		return graphModel;
	}

	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;
	}

	public DynaFormModel getPropertyEditorModel() {
		return propertyEditorModel;
	}

	public void setPropertyEditorModel(DynaFormModel propertyEditorModel) {
		this.propertyEditorModel = propertyEditorModel;
	}

	public String getGeneratedCode() {
		return generatedCode;
	}

	public void setGeneratedCode(String generatedCode) {
		this.generatedCode = generatedCode;
	}

	public List<GraphValidationError> getGraphValidationErrors() {
		return graphValidationErrors;
	}

	public void setGraphValidationErrors(List<GraphValidationError> graphValidationErrors) {
		this.graphValidationErrors = graphValidationErrors;
	}

	public List<GraphValidationWarning> getGraphValidationWarnings() {
		return graphValidationWarnings;
	}

	public void setGraphValidationWarnings(List<GraphValidationWarning> graphValidationWarnings) {
		this.graphValidationWarnings = graphValidationWarnings;
	}

	public Map<String, List<GraphNode>> getAvailableNodesByTypeMap() {
		return availableNodesByTypeMap;
	}

	public int getSelectedConsoleTabIndex() {
		return selectedConsoleTabIndex;
	}

	public void setSelectedConsoleTabIndex(int selectedConsoleTabIndex) {
		this.selectedConsoleTabIndex = selectedConsoleTabIndex;
	}

	public void cleanupWorkspace() {
		if (graphModel != null) {
			graphModel.clear();
		}
		graphValidationErrors.clear();
		graphValidationWarnings.clear();
		generatedCode = null;

		filterLocationLat = 0;
		filterLocationLon = 0;
		filterLocationRadius = 0;
		clearAvailableSensors();
	}

	// --------------------------------------------------------------------------
	// Sensor lookup dialog
	// --------------------------------------------------------------------------

	public double getFilterLocationLat() {
		return filterLocationLat;
	}

	public void setFilterLocationLat(double filterLocationLat) {
		this.filterLocationLat = filterLocationLat;
	}

	public double getFilterLocationLon() {
		return filterLocationLon;
	}

	public void setFilterLocationLon(double filterLocationLon) {
		this.filterLocationLon = filterLocationLon;
	}

	public double getFilterLocationRadius() {
		return filterLocationRadius;
	}

	public void setFilterLocationRadius(double filterLocationRadius) {
		this.filterLocationRadius = filterLocationRadius;
	}

	public void clearAvailableSensors() {
		List<GraphNode> sensorList = availableNodesByTypeMap.get("SOURCE");
		sensorList.clear();
	}

	public void updateAvailableSensors(SensorTypes sensorTypes) {
		List<GraphNode> sensorList = availableNodesByTypeMap.get("SOURCE");
		sensorList.clear();
		for (SensorType sensorType : sensorTypes.getSensorType()) {
			GenericSource source = new GenericSource();
			source.setLabel(sensorType.getName());
			source.setType("SOURCE");

			// Copy selected filter params
			source.getPropertyValueMap().put("LAT", filterLocationLat);
			source.getPropertyValueMap().put("LON", filterLocationLon);
			source.getPropertyValueMap().put("RADIUS", filterLocationRadius);

			// Initialize sensor endpoints
			List<GraphNodeEndpoint> endpointList = new ArrayList<GraphNodeEndpoint>();
			source.setEndpointDefinitions(endpointList);

			// Add an additional endpoint for filtering options
			GraphNodeEndpoint endpoint = new DefaultGraphNodeEndpoint();
			endpoint.setAnchor(AnchorType.Left);
			endpoint.setConnectorType(ConnectorType.Dot);
			endpoint.setMaxConnections(1);
			endpoint.setRequired(false);
			endpoint.setType(EndpointType.Input);
			endpoint.setLabel("SEL_FILTER_IN");
			endpoint.setScope("Sensor");
			endpointList.add(endpoint);

			for (MeasurementCapability cap : sensorType.getMeasurementCapability()) {
				if (cap.getUnit() == null || cap.getUnit().isEmpty()) {
					continue;
				}

				endpoint = new DefaultGraphNodeEndpoint();
				endpoint.setAnchor(AnchorType.Right);
				endpoint.setConnectorType(ConnectorType.Rectangle);
				endpoint.setMaxConnections(-1);
				endpoint.setRequired(false);
				endpoint.setType(EndpointType.Output);
				String label = cap.getType();
				if (label.contains("#")) {
					label = label.substring(label.indexOf('#') + 1);
				}
				if (!cap.getUnit().isEmpty() && cap.getUnit().get(0).getName() != null && !cap.getUnit().get(0).getName().equals("null") && !cap.getUnit().get(0).getName().isEmpty()) {
					label += " (" + cap.getUnit().get(0).getName() + ")";
				}
				endpoint.setLabel(label);
				endpoint.setUserData(cap.getType());

				String scope = "Number";
				String capScope = cap.getUnit().get(0).getType();
				if (StringUtils.containsIgnoreCase(capScope, "Int")) {
					scope = "Integer";
				} else if (StringUtils.containsIgnoreCase(capScope, "Long")) {
					scope = "Long";
				} else if (StringUtils.containsIgnoreCase(capScope, "Float")) {
					scope = "Float";
				} else if (StringUtils.containsIgnoreCase(capScope, "Double")) {
					scope = "Double";
				} else if (StringUtils.containsIgnoreCase(capScope, "Decimal")) {
					scope = "Number";
				} else if (StringUtils.containsIgnoreCase(capScope, "Date")) {
					scope = "Date";
				}

				endpoint.setScope("sensor_" + scope);

				// Add to endpoint list
				endpointList.add(endpoint);
			}

			// Add additional endpoint for sensor lat
			endpoint = new DefaultGraphNodeEndpoint();
			endpoint.setAnchor(AnchorType.Right);
			endpoint.setConnectorType(ConnectorType.Rectangle);
			endpoint.setMaxConnections(-1);
			endpoint.setRequired(false);
			endpoint.setType(EndpointType.Output);
			endpoint.setLabel("LAT");
			endpoint.setUserData("geo:lat");
			endpoint.setScope("geo_lat");
			endpointList.add(endpoint);

			// Add additional endpoint for sensor lon
			endpoint = new DefaultGraphNodeEndpoint();
			endpoint.setAnchor(AnchorType.Right);
			endpoint.setConnectorType(ConnectorType.Rectangle);
			endpoint.setMaxConnections(-1);
			endpoint.setRequired(false);
			endpoint.setType(EndpointType.Output);
			endpoint.setLabel("LON");
			endpoint.setUserData("geo:lon");
			endpoint.setScope("geo_lon");
			endpointList.add(endpoint);

			// Add sensor to toolbox
			sensorList.add(source);
		}
	}

	// --------------------------------------------------------------------------
	// New service dialog
	// --------------------------------------------------------------------------

	public String getNewApplicationName() {
		return newApplicationName;
	}

	public void setNewApplicationName(String newServiceName) {
		this.newApplicationName = newServiceName;
	}

	public String getNewApplicationDescription() {
		return newApplicationDescription;
	}

	public void setNewApplicationDescription(String newApplicationDescription) {
		this.newApplicationDescription = newApplicationDescription;
	}

	// --------------------------------------------------------------------------
	// Import/export
	// --------------------------------------------------------------------------

	public boolean isPersistSpec() {
		return persistSpec;
	}

	public StreamedContent getExportedSpec() {
		return exportedSpec;
	}

	public void setExportedSpec(StreamedContent exportedSpec) {
		this.exportedSpec = exportedSpec;
	}

	public UploadedFile getUploadedSpec() {
		return uploadedSpec;
	}

	public void setUploadedSpec(UploadedFile uploadedSpec) {
		this.uploadedSpec = uploadedSpec;
	}

	public void setPersistSpec(boolean persistSpec) {
		this.persistSpec = persistSpec;
	}

	// --------------------------------------------------------------------------
	// GraphNode annotation scanner
	// --------------------------------------------------------------------------
	private void detectAvailableNodes() {
		availableNodesByTypeMap.clear();
		availableNodesByTypeMap.put("SOURCE", new ArrayList<GraphNode>());

		LOGGER.debug("[ServiceDesignPageContext] Scanning for available graph node classes");
		Set<Class<?>> graphNodeClasses = GraphNodeScanner.detectGraphNodeClasses("org.openiot.ui.request.definition.web.model.nodes.impl");
		if (graphNodeClasses == null) {
			LOGGER.warn("[ServiceDesignPageContext] No graph node classes detected");
			return;
		}

		for (Class<?> classDefinition : graphNodeClasses) {
			try {
				LOGGER.debug("[ServiceDesignPageContext]   Detected graph node class: " + classDefinition.getCanonicalName());
				GraphNode instance = (GraphNode) (classDefinition.newInstance());

				// Lookup type
				String type = instance.getType();
				if (type == null) {
					type = "UNKNOWN";
				}
				List<GraphNode> groupList = availableNodesByTypeMap.get(type);
				if (groupList == null) {
					groupList = new ArrayList<GraphNode>();
					availableNodesByTypeMap.put(type, groupList);
				}

				groupList.add(instance);

			} catch (Throwable ex) {
				LOGGER.error("", ex);
			}
		}
	}

}
