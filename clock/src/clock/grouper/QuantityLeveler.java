package clock.grouper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class QuantityLeveler {
	private QuantityLeveler() {
		throw new AssertionError();
	}

	public static QuantityLevelProvider getQuantityLevelProvider(double minimumLevelValue, double maximumLevelValue,
			int minimumInputValue, int maximumInputValue) {
		return new QuantityLevelProvider(minimumLevelValue, maximumLevelValue, minimumInputValue, maximumInputValue);
	}

	public static final class QuantityLevelProvider {
		private static final double TARGET_NUMBER_OF_GROUPS = 6;

		private final Map<Integer, QuantityLevel> granulationMap = new HashMap<>();

		private final int granulationUnit;

		private QuantityLevelProvider(double minimumLevelValue, double maximumLevelValue, int minimumInputValue,
				int maximumInputValue) {
			granulationUnit = getGranulationUnit(minimumInputValue, maximumInputValue);
			int minimumGranulationLevel = minimumInputValue / granulationUnit;
			int maximumGranulationLevel = maximumInputValue / granulationUnit;

			double levelValueDelta = (maximumLevelValue - minimumLevelValue)
					/ (maximumGranulationLevel - minimumGranulationLevel);

			double currentLevelValue = minimumLevelValue;
			for (int currentValue = minimumGranulationLevel; currentValue <= maximumGranulationLevel; currentValue++) {
				granulationMap.put(currentValue,
						new QuantityLevel(currentLevelValue, getLevelDescription(currentValue * granulationUnit)));
				currentLevelValue += levelValueDelta;
			}

			System.out.println("-------------");
			System.out.printf("Min input value: %d\n", minimumInputValue);
			System.out.printf("Max input value: %d\n", maximumInputValue);
			System.out.printf("Granulation unit: %d\n", granulationUnit);
		}

		private String getLevelDescription(int currentValue) {
			if (granulationUnit == 1) {
				return String.valueOf(currentValue);
			} else {
				return String.format("%d - %d", currentValue, currentValue + granulationUnit - 1);
			}
		}

		private int getGranulationUnit(int minimumInputValue, int maximumInputValue) {
			int bestGranulationLevel = 1;
			double bestGranulationLevelEfficiency = 0;

			for (int granulationLevelPretendant : getGranulationUnits()) {
				int numberOfGroupsForGivenGranulationLevel = 1 + maximumInputValue / granulationLevelPretendant
						- (minimumInputValue / granulationLevelPretendant);

				double granulationLevelPretendantEfficiency = (double) numberOfGroupsForGivenGranulationLevel
						/ TARGET_NUMBER_OF_GROUPS;

				if (granulationLevelPretendantEfficiency > 1) {
					granulationLevelPretendantEfficiency = 1 / granulationLevelPretendantEfficiency;
				}

				if (granulationLevelPretendantEfficiency > bestGranulationLevelEfficiency) {
					bestGranulationLevel = granulationLevelPretendant;
					bestGranulationLevelEfficiency = granulationLevelPretendantEfficiency;
				} else {
					break;
				}
			}

			return bestGranulationLevel;
		}

		private static Iterable<Integer> getGranulationUnits() {
			return new Iterable<Integer>() {
				@Override
				public Iterator<Integer> iterator() {
					return new Iterator<Integer>() {
						private boolean isAtTheBeginning = true;
						private int currentValue = 0;
						private int currentMultiplier = 1;

						@Override
						public boolean hasNext() {
							return currentMultiplier < 1000000000;
						}

						@Override
						public Integer next() {
							if (isAtTheBeginning) {
								switch (currentValue) {
								case 0:
									currentValue = 1;
									break;
								case 1:
									currentValue = 2;
									break;
								case 2:
									currentValue = 3;
									break;
								case 3:
									currentValue = 5;
									break;
								default:
									currentValue = 10;
									isAtTheBeginning = false;
									break;
								}
							} else {
								switch (currentValue) {
								case 10:
									currentValue = 20;
									break;
								case 20:
									currentValue = 25;
									break;
								case 25:
									currentValue = 50;
									break;
								default:
									currentValue = 10;
									currentMultiplier *= 10;
									break;
								}
							}

							return currentValue * currentMultiplier;
						}

						@Override
						public void remove() {
						}
					};
				}
			};
		}

		public QuantityLevel getLevelForQuantity(int quantity) {
			return granulationMap.get(quantity / granulationUnit);
		}
	}

	public static class QuantityLevel {
		private final double levelValue;
		private String levelDescription;

		private QuantityLevel(double levelValue, String levelDescription) {
			this.levelValue = levelValue;
			this.setLevelDescription(levelDescription);

		}

		public double getLevelValue() {
			return levelValue;
		}

		public String getLevelDescription() {
			return levelDescription;
		}

		public void setLevelDescription(String levelDescription) {
			this.levelDescription = levelDescription;
		}
	}
}
