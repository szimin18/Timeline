package histogram.view;

import histogram.event.HistogramFilterChangeEvent;
import histogram.event.HistogramFilterChangeListener;
import histogram.grouper.Grouper;
import histogram.grouper.Grouper.GroupingMethod;
import histogram.selector.Selector;
import histogram.selector.Selector.TimelineTick;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Transform;
import javafx.stage.Popup;
import javafx.stage.PopupBuilder;
import javafx.util.Pair;
import model.dataset.TimelineDataSet;
import model.event.TimelineCategory;
import model.event.TimelineChartData;

@SuppressWarnings("deprecation")
public class Histogram extends Pane {

	private static final String SELECTED_STYLE = "-fx-border-color: black; -fx-border-width: 3; -fx-border-style: dotted";
	private static final String NOT_SELECTED_STYLE = "-fx-bar-fill: %s;";
	

	private final GroupingMethod defaultGroupingMethod;

	private GroupingMethod groupingMethod;

	private StackPane currentStackPane = null;

	private ChangeListener<Number> widthChangeListener = null;

	private ChangeListener<Number> heightchangeListener = null;

	private final List<TimelineDataSet> timelineDataSets;

	private List<TimelineCategory> groupedCategories;

	private final List<HistogramFilterChangeListener> filterChangeListeners;
	
	private final Map<Bounds, Pair<TimelineChartData, Node>> boundsToBars; //TODO should be replaced with sth more efficient 
	
	private final Set<TimelineChartData> selected;
	
	private final Map<Bounds, Bounds> boundsToTransformedBounds;
	
	private Popup popup;
	
	private final Label popupLabel;

	public Histogram(List<TimelineDataSet> timelineDataSets) {
		this.timelineDataSets = timelineDataSets;

		defaultGroupingMethod = Grouper.getDefaultGroupingMethodForDatasets(timelineDataSets);

		groupingMethod = defaultGroupingMethod;

		filterChangeListeners = new LinkedList<>();
		
		boundsToBars = new HashMap<>();
		
		boundsToTransformedBounds = new HashMap<>();
		
		selected = new HashSet<>();
		
		popupLabel = new Label();
		
		setupPopup();
		
		initializeGUI();
	}

	public Histogram(List<TimelineDataSet> timelineDataSets, GroupingMethod groupingMethod) {
		this.timelineDataSets = timelineDataSets;

		defaultGroupingMethod = Grouper.getDefaultGroupingMethodForDatasets(timelineDataSets);

		this.groupingMethod = groupingMethod;

		filterChangeListeners = new LinkedList<>();
		
		boundsToBars = new HashMap<>();
		
		boundsToTransformedBounds = new HashMap<>();
		
		selected = new HashSet<>();

		popupLabel = new Label();
		
		setupPopup();
		
		initializeGUI();
	}

	private void setupPopup() {
		popupLabel.setStyle("-fx-background-color: white;-fx-border-color: black");
		popup = PopupBuilder.create().content(popupLabel).width(200).height(50).autoFix(true).build();
	}

	public void setGroupingMethod(GroupingMethod groupingMethod) {
		this.groupingMethod = groupingMethod;
	}

	public void restoreDefaultGroupingMethod() {
		groupingMethod = defaultGroupingMethod;
	}

	private void initializeGUI() {
		if (widthChangeListener != null) {
			widthProperty().removeListener(widthChangeListener);
			widthChangeListener = null;
		}
		if (heightchangeListener != null) {
			heightProperty().removeListener(heightchangeListener);
			heightchangeListener = null;
		}
		if (currentStackPane != null) {
			getChildren().removeAll(currentStackPane);
			currentStackPane = null;
		}

		groupedCategories = Grouper.group(timelineDataSets, groupingMethod);

		final StackPane stackPane = new StackPane();
		getChildren().addAll(stackPane);
		currentStackPane = stackPane;
		stackPane.setAlignment(Pos.TOP_LEFT);

		widthChangeListener = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double doubleValue = newValue.doubleValue();
				stackPane.setMinWidth(doubleValue);
				stackPane.setMaxWidth(doubleValue);
			};
		};
		widthProperty().addListener(widthChangeListener);

		heightchangeListener = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double doubleValue = newValue.doubleValue();
				stackPane.setMinHeight(doubleValue);
				stackPane.setMaxHeight(doubleValue);
			};
		};
		heightProperty().addListener(heightchangeListener);

		final Selector selector = new Selector(this);

		CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();

		final StackedBarChart<String, Number> chart = new StackedBarChart<>(xAxis, yAxis);

		chart.setCategoryGap(1);
		chart.setLegendVisible(false);

		stackPane.getChildren().addAll(chart, selector);

		stackPane.widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double doubleValue = newValue.doubleValue();
				chart.setMinWidth(doubleValue);
				chart.setMaxWidth(doubleValue);
				selector.widthProperty().set(doubleValue);
			};
		});

		stackPane.heightProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double doubleValue = newValue.doubleValue();
				chart.setMinHeight(doubleValue);
				chart.setMaxHeight(doubleValue);
				selector.heightProperty().set(doubleValue);
			};
		});

		chart.getXAxis().localToSceneTransformProperty().addListener(new ChangeListener<Transform>() {
			@Override
			public void changed(ObservableValue<? extends Transform> observable, Transform oldValue, Transform newValue) {
				selector.setLeftX(newValue.getTx());
			}
		});

		chart.getXAxis().widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				selector.setChartWidth(newValue.doubleValue());
			}
		});

		chart.getYAxis().localToSceneTransformProperty().addListener(new ChangeListener<Transform>() {
			public void changed(ObservableValue<? extends Transform> observable, Transform oldValue, Transform newValue) {
				selector.setTopY(newValue.getTy());
			};
		});

		chart.getYAxis().heightProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				selector.setChartHeight(newValue.doubleValue());
			};
		});

		ObservableList<String> categoriesNamesList = FXCollections.<String> observableArrayList();

		xAxis.setCategories(categoriesNamesList);

		chart.setTitle("Histogram");
		xAxis.setLabel("Event time periods");
		yAxis.setLabel("Number of occurences");

		ObservableList<Series<String, Number>> chartData = chart.getData();

		boolean firstSeries = true;

		for (TimelineCategory timelineCategory : groupedCategories) {
			Series<String, Number> series = new Series<>();

			ObservableList<Data<String, Number>> seriesData = series.getData();

			chartData.add(series);

			for (final TimelineChartData timelineChartData : timelineCategory.getTimelineChartDataList()) {
				Data<String, Number> data = new Data<String, Number>(timelineChartData.getDescription(),
						timelineChartData.getEventsCount());
				seriesData.add(data);

				final Node node = data.getNode();
				node.setStyle(String.format(NOT_SELECTED_STYLE, timelineCategory.getColorHex()));
				 
				node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
					public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
						Node parent = node.getParent();
						Bounds newTranformed = parent.localToScene(newValue);

						if (boundsToTransformedBounds.containsKey(oldValue)) {
							Bounds oldTransformed = boundsToTransformedBounds.get(oldValue);
							boundsToBars.remove(oldTransformed);
							boundsToTransformedBounds.remove(oldValue);
						}
						boundsToTransformedBounds.put(newValue, newTranformed);
						boundsToBars.put(newTranformed, new Pair<>(timelineChartData, node));
					};
				});
				
				if (firstSeries) {
					categoriesNamesList.add(data.getXValue());

					final TimelineTick timelineTick = selector.newTimelineTick();

					node.localToSceneTransformProperty().addListener(new ChangeListener<Transform>() {
						public void changed(ObservableValue<? extends Transform> observable, Transform oldValue,
								Transform newValue) {
							timelineTick.setLeft(newValue.getTx());
							selector.drawFrame();
						};
					});

					node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
						public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue,
								Bounds newValue) {
							timelineTick.setWidth(newValue.getWidth());
							selector.drawFrame();
						};
					});
				}
			}

			firstSeries = false;
		}
		
		//TODO should be replaced with sth more efficient 
		addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				boolean found = false;
				for (Bounds bounds : boundsToBars.keySet()) {
					if (bounds.contains(localToScene(event.getX() - yAxis.getWidth(), event.getY()))) {
						TimelineChartData data = boundsToBars.get(bounds).getKey();
						found = true;
						popupLabel.setText(String.format("%s\n%d events", data.getDescription(), data.getEventsCount()));
						popup.setAnchorX(event.getScreenX()+ 20);
						popup.setAnchorY(event.getScreenY() + 20);
						popup.show(Histogram.this.getScene().getWindow());
						break;
					}
				}
				if (!found) {
					popup.hide();
				}
			}
		});
		
		addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				for (Bounds bounds : boundsToBars.keySet()) {
					if (bounds.contains(localToScene(event.getX(), event.getY()))) {
						TimelineChartData data = boundsToBars.get(bounds).getKey();
						Node node = boundsToBars.get(bounds).getValue();
						if (selected.contains(data)) {
							selected.remove(data);
							node.setStyle(node.getStyle().split(";")[0] + ";");
							node.applyCss();
						} else {
							selected.add(data);
							node.setStyle(node.getStyle() + SELECTED_STYLE);
							node.applyCss();
						}
						System.out.println(selected.size());
						break;
					}
				}
			}
		});
	}

	public void addFilterChangeListener(HistogramFilterChangeListener listener) {
		filterChangeListeners.add(listener);
	}

	public void removeFilterChangeListener(HistogramFilterChangeListener listener) {
		filterChangeListeners.remove(listener);
	}

	public void selectionChanged(int currentLeftTickIndex, int currentRightTickIndex) {
		Date beginning = groupedCategories.get(0).getTimelineChartDataList().get(currentLeftTickIndex).getBeginning();
		Date end = groupedCategories.get(0).getTimelineChartDataList().get(currentRightTickIndex).getEnd();
		HistogramFilterChangeEvent event = new HistogramFilterChangeEvent(this, beginning, end,
				currentLeftTickIndex, currentRightTickIndex);
		for (HistogramFilterChangeListener listener : filterChangeListeners) {
			listener.selectionChanged(event);
		}
	}

	public List<TimelineCategory> getFilteredEvents(int firstElement, int lastElement) {
		List<TimelineCategory> result = new ArrayList<>(groupedCategories.size());
		for (TimelineCategory category : groupedCategories) {
			TimelineCategory newCategory = new TimelineCategory(category.getColor());
			for (int i = firstElement; i <= lastElement; i++) {
				newCategory.addTimelineChartData(category.getTimelineChartDataList().get(i));
			}
			result.add(newCategory);
		}
		return result;
	}
}
