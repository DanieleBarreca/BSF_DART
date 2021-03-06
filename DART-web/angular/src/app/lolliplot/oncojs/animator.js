import { shouldAnimationFinish, calculateNextCoordinate } from './animation';
import { halfPixel } from './spatial';
import { updateStats } from './stats';
import theme from './theme';

var animator = function animator(_ref) {
  var d3 = _ref.d3,
      d3Root = _ref.d3Root,
      store = _ref.store,
      data = _ref.data,
      yAxisOffset = _ref.yAxisOffset,
      xAxisOffset = _ref.xAxisOffset,
      statsBoxWidth = _ref.statsBoxWidth,
      height = _ref.height,
      width = _ref.width,
      domainWidth = _ref.domainWidth,
      scale = _ref.scale,
      numXTicks = _ref.numXTicks,
      mutationChartLines = _ref.mutationChartLines,
      mutationChartCircles = _ref.mutationChartCircles,
      selectedMutationBox = _ref.selectedMutationBox,
      consequences = _ref.consequences,
      impacts = _ref.impacts,
      consequenceColors = _ref.consequenceColors,
      consequencesCheckboxContainers = _ref.consequencesCheckboxContainers,
      impactsCheckboxContainers = _ref.impactsCheckboxContainers,
      proteinBars = _ref.proteinBars,
      proteinClipPaths = _ref.proteinClipPaths,
      proteinNames = _ref.proteinNames,
      xTicks = _ref.xTicks,
      yTicks = _ref.yTicks,
      yTicksLine = _ref.yTicksLine,
      minimapZoomArea = _ref.minimapZoomArea,
      minimapSlideTarget = _ref.minimapSlideTarget,
      minimapSlideTargetArrow = _ref.minimapSlideTargetArrow,
      mutationCount = _ref.mutationCount,
      animate = _ref.animate,
      hideStats = _ref.hideStats,
      currentLine = _ref.currentLine,
      otherLines = _ref.otherLines,
      otherCircles = _ref.otherCircles;


  var totalAnimationIterations = animate ? 30 : 1;

  var draw = function draw() {
    var _store$getState = store.getState(),
        targetMin = _store$getState.targetMin,
        targetMax = _store$getState.targetMax,
        startMin = _store$getState.startMin,
        startMax = _store$getState.startMax,
        currentAnimationIteration = _store$getState.currentAnimationIteration,
        consequenceFilters = _store$getState.consequenceFilters,
        impactFilters = _store$getState.impactFilters;

    var min = animate ? calculateNextCoordinate({
      start: startMin, target: targetMin, currentAnimationIteration: currentAnimationIteration, totalAnimationIterations: totalAnimationIterations
    }) : targetMin;

    var max = animate ? calculateNextCoordinate({
      start: startMax, target: targetMax, currentAnimationIteration: currentAnimationIteration, totalAnimationIterations: totalAnimationIterations
    }) : targetMax;

    var domain = max - min;

    store.update({ min: min, max: max, domain: domain, currentAnimationIteration: currentAnimationIteration + 1 });

    if (shouldAnimationFinish({ startMin: startMin, startMax: startMax, targetMin: targetMin, targetMax: targetMax, min: min, max: max })) {
      store.update({
        animating: false,
        startMin: min,
        startMax: max,
        currentAnimationIteration: 0
      });
    }

    var scaleLinear = d3.scaleLinear().domain([min, max]).range([yAxisOffset, width - statsBoxWidth]);

    var widthZoomRatio = domainWidth / Math.max(max - min, 0.00001); // Do not divide by zero

    // Proteins

    proteinBars.attr('x', function (d) {
      return Math.max(yAxisOffset, scaleLinear(d.start)) + halfPixel;
    }).attr('width', function (d) {
      var barWidth = (d.end - Math.max(d.start, min)) * widthZoomRatio * scale;
      return Math.max(0, barWidth - 1);
    });

    proteinClipPaths.attr('x', function (d) {
      return Math.max(yAxisOffset, scaleLinear(d.start)) + halfPixel;
    }).attr('width', function (d) {
      var barWidth = (d.end - Math.max(d.start, min)) * widthZoomRatio * scale;
      return Math.max(0, barWidth - 1);
    });

    proteinNames.attr('x', function (d) {
      var barWidth = (d.end - Math.max(d.start, min)) * widthZoomRatio * scale;
      var x = scaleLinear(d.start);
      return barWidth + yAxisOffset < yAxisOffset ? x : Math.max(yAxisOffset, x);
    });

    // Horizontal ticks
    var length = domain / numXTicks;
    xTicks.text(function (i) {
      return Math.round(length * i + min);
    });

    // Minimap zoom area

    var minimapWidth = Math.max(1, (max - min) * scale - 1);

    minimapZoomArea.attr('x', min * scale + yAxisOffset + halfPixel).attr('width', minimapWidth);

    minimapSlideTarget.attr('x', min * scale + yAxisOffset + halfPixel + minimapWidth - 20);

    minimapSlideTargetArrow.attr('x', min * scale + yAxisOffset + halfPixel + minimapWidth - 19);

    mutationChartLines.attr('x1', function (d) {
      return scaleLinear(d.x);
    }).attr('x2', function (d) {
      return scaleLinear(d.x);
    });

    currentLine.attr('x1', function (d) {
      return scaleLinear(d.x);
    }).attr('x2', function (d) {
      return scaleLinear(d.x);
    });

    otherLines.attr('x1', function (d) {
      return scaleLinear(d.x);
    }).attr('x2', function (d) {
      return scaleLinear(d.x);
    });

    mutationChartCircles.attr('cx', function (d) {
      return scaleLinear(d.x);
    });

    otherCircles.attr('x', function (d) {
      return scaleLinear(d.x)-theme.mutationRadius;
    });

    selectedMutationBox.attr('x', function (d) {
      return scaleLinear(d.x) - d.size / 2;
    });

    animateScaleY({
      d3: d3,
      d3Root: d3Root,
      data: data,
      consequenceFilters: consequenceFilters,
      impactFilters: impactFilters,
      min: min,
      max: max,
      mutationChartLines: mutationChartLines,
      mutationChartCircles: mutationChartCircles,
      selectedMutationBox: selectedMutationBox,
      height: height,
      xAxisOffset: xAxisOffset,
      visibleMutations: null,
      yTicks: yTicks,
      yTicksLine: yTicksLine
    });

    if (!hideStats) {
      updateStats({
        d3: d3,
        d3Root: d3Root,
        store: store,
        data: data,
        consequences: consequences,
        impacts: impacts,
        consequenceColors: consequenceColors,
        mutationChartLines: mutationChartLines,
        mutationChartCircles: mutationChartCircles,
        selectedMutationBox: selectedMutationBox,
        height: height,
        xAxisOffset: xAxisOffset,
        consequencesCheckboxContainers: consequencesCheckboxContainers,
        impactsCheckboxContainers: impactsCheckboxContainers,
        mutationCount: mutationCount,
        yTicks: yTicks,
        yTicksLine: yTicksLine,
        otherCircles: otherCircles,
        otherLines: otherLines
      });
    }

    if (store.getState().animating) window.requestAnimationFrame(draw);
  };

  return draw;
};

var animateScaleY = function animateScaleY(_ref2) {
  var d3 = _ref2.d3,
      data = _ref2.data,
      consequenceFilters = _ref2.consequenceFilters,
      impactFilters = _ref2.impactFilters,
      min = _ref2.min,
      max = _ref2.max,
      mutationChartLines = _ref2.mutationChartLines,
      mutationChartCircles = _ref2.mutationChartCircles,
      selectedMutationBox = _ref2.selectedMutationBox,
      height = _ref2.height,
      xAxisOffset = _ref2.xAxisOffset,
      vm = _ref2.visibleMutations,
      yTicks = _ref2.yTicks,
      yTicksLine = _ref2.yTicksLine;

  var visibleMutations = vm || data.mutations.filter(function (d) {
    return d.x > min && d.x < max && !consequenceFilters.includes(d.consequence) && !impactFilters.includes(d.impact);
  });

  if (visibleMutations.length) {
    (function () {

      var maxDonors = Math.max.apply(Math, visibleMutations.map(function (x) {
        return x.donors;
      }));

      var highestValue = Math.max(10, maxDonors);

      var scaleLinearY = d3.scaleLinear().domain([0, highestValue]).range([height - xAxisOffset, 15]);

      mutationChartLines.attr('y2', function (d) {
        return scaleLinearY(d.donors);
      });

      mutationChartCircles.attr('cy', function (d) {
        return scaleLinearY(d.donors);
      });

      selectedMutationBox.attr('y', function (d) {
        return scaleLinearY(d.donors) - d.size / 2;
      });

      // Vertical ticks

      yTicks.attr('y', function (i) {
        return scaleLinearY(i) + 3;
      });

      yTicksLine.attr('y1', function (i) {
        return scaleLinearY(i);
      }).attr('y2', function (i) {
        return scaleLinearY(i);
      });
    })();
  }
};

/*----------------------------------------------------------------------------*/

export default animator;
export { animateScaleY };