import invariant from 'invariant';
import attrs from './attrs';
import { dim, halfPixel } from './spatial';
import setupStore from './store';
import setupFilters from './filters';
import setupMinimap from './minimap';
import setupProteins from './proteins';
import { setupMutations, updateMutations } from './mutations';
import {setupLines, updateLines} from './lines'
import { setupStats, updateStats } from './stats';
import setupTicks from './ticks';
import setupZoomHandlers from './zoom';
import theme from './theme';
import groupByType from './groupByType';
import animator from './animator';
import uuid from './uuid';

/*----------------------------------------------------------------------------*/

var proteinLolliplot = function proteinLolliplot() {
  var _ref = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {},
    d3 = _ref.d3,
    data = _ref.data,
    selector = _ref.selector,
    element = _ref.element,
    height = _ref.height,
    width = _ref.width,
    currentPosition = _ref.currentPosition,
    otherPositions = _ref.otherPositions,
    _ref$domainWidth = _ref.domainWidth,
    domainWidth = _ref$domainWidth === undefined ? 500 : _ref$domainWidth,
    _ref$hideStats = _ref.hideStats,
    hideStats = _ref$hideStats === undefined ? false : _ref$hideStats,
    _ref$statsBoxWidth = _ref.statsBoxWidth,
    statsBoxWidth = _ref$statsBoxWidth === undefined ? hideStats ? 0 : 250 : _ref$statsBoxWidth,
    _ref$mutationId = _ref.mutationId,
    mutationId = _ref$mutationId === undefined ? '' : _ref$mutationId,
    _ref$yAxisOffset = _ref.yAxisOffset,
    yAxisOffset = _ref$yAxisOffset === undefined ? 45 : _ref$yAxisOffset,
    _ref$xAxisOffset = _ref.xAxisOffset,
    xAxisOffset = _ref$xAxisOffset === undefined ? 200 : _ref$xAxisOffset,
    _ref$store = _ref.store,
    store = _ref$store === undefined ? setupStore({ domainWidth: domainWidth, data: data }) : _ref$store,
    _ref$proteinHeight = _ref.proteinHeight,
    proteinHeight = _ref$proteinHeight === undefined ? 40 : _ref$proteinHeight,
    _ref$numXTicks = _ref.numXTicks,
    numXTicks = _ref$numXTicks === undefined ? 12 : _ref$numXTicks,
    _ref$numYTicks = _ref.numYTicks,
    numYTicks = _ref$numYTicks === undefined ? 15 : _ref$numYTicks,
    _ref$proteinDb = _ref.proteinDb,
    proteinDb = _ref$proteinDb === undefined ? 'pfam' : _ref$proteinDb,
    _ref$animate = _ref.animate,
    animate = _ref$animate === undefined ? true : _ref$animate,
    _ref$hasCustomMutatio = _ref.hasCustomMutationColor,
    hasCustomMutationColor = _ref$hasCustomMutatio === undefined ? false : _ref$hasCustomMutatio,
    _ref$getMutationColor = _ref.getMutationColor,
    getMutationColor = _ref$getMutationColor === undefined ? function () { } : _ref$getMutationColor,
    _ref$onMutationClick = _ref.onMutationClick,
    onMutationClick = _ref$onMutationClick === undefined ? function () { } : _ref$onMutationClick,
    _ref$onMutationMouseo = _ref.onMutationMouseover,
    onMutationMouseover = _ref$onMutationMouseo === undefined ? function () { } : _ref$onMutationMouseo,
    _ref$onMutationMouseo2 = _ref.onMutationMouseout,
    onMutationMouseout = _ref$onMutationMouseo2 === undefined ? function () { } : _ref$onMutationMouseo2,
    _ref$onProteinMouseov = _ref.onProteinMouseover,
    onProteinMouseover = _ref$onProteinMouseov === undefined ? function () { } : _ref$onProteinMouseov,
    _ref$onProteinMouseou = _ref.onProteinMouseout,
    onProteinMouseout = _ref$onProteinMouseou === undefined ? function () { } : _ref$onProteinMouseou,
    _ref$onInit = _ref.onInit,
    onInit = _ref$onInit === undefined ? function () { } : _ref$onInit,
    logoElement = _ref.logoElement;

  invariant(d3, 'You must pass in the d3 library, either v3 || v4');
  d3.selection.prototype.attrs = attrs;
  //d3.scaleOrdinal = d3.scaleOrdinal || d3.scale.ordinal;
  //d3.scaleLinear = d3.scaleLinear || d3.scale.linear;

  // Similar to a React target element
  var root = element || document.querySelector(selector);

  invariant(root, 'Must provide an element or selector!');

  width = width || root.clientWidth;
  height = height || root.clientHeight;

  var uniqueSelector = uuid();
  var xAxisLength = width - yAxisOffset - statsBoxWidth;
  var scale = xAxisLength / domainWidth;

  var consequences = hasCustomMutationColor ? {} : groupByType('consequence', data.mutations);
  var impacts = hasCustomMutationColor ? {} : groupByType('impact', data.mutations);

  var colorScale = d3.schemeCategory20 ? d3.scaleOrdinal(d3.schemeCategory20).domain(d3.range(20)) : d3.scale.category20().domain(d3.range(20));

  var consequenceColors = hasCustomMutationColor ? {} : Object.keys(consequences).reduce(function (acc, type, i) {
    var _Object$assign;

    return Object.assign({}, acc, (_Object$assign = {}, _Object$assign[type] = colorScale(i * 3), _Object$assign));
  }, {});

  var maxDonors = Math.max.apply(Math, data.mutations.map(function (x) {
    return x.donors;
  }));

  var highestValue = Math.max(10, maxDonors);

  var scaleLinearY = d3.scaleLinear().domain([0, highestValue]).range([height - xAxisOffset, 15]);

  // Main Chart

  var d3Root = d3.select(root).style('position', 'relative');

  var svg = d3Root.append('svg').attrs(Object.assign({
    class: 'chart'
  }, dim(width, height)));

  var defs = svg.append('defs');

  setupFilters(defs);

  // Chart clipPath

  defs.append('clipPath').attr('id', uniqueSelector + '-chart-clip').append('rect').attrs(Object.assign({
    x: yAxisOffset,
    y: 0
  }, dim(xAxisLength, height - xAxisOffset + proteinHeight)));

  // Chart zoom area

  var chart = d3Root.select('.chart');

  chart.append('rect').attrs(Object.assign({
    class: 'chart-zoom-area',
    x: yAxisOffset,
    y: halfPixel
  }, dim(xAxisLength, height - xAxisOffset + proteinHeight - halfPixel), {
      fill: 'white',
      stroke: 'rgb(181, 181, 181)',
      'stroke-width': 1
    }));

  // yAxis

  svg.append('g').append('line').attrs({
    class: 'yAxis',
    x1: yAxisOffset,
    y1: 0,
    x2: yAxisOffset,
    y2: height - xAxisOffset + proteinHeight,
    stroke: theme.black
  });


  // yAxis label

  chart.append('text').text('# of Cases').attrs({
    x: 5,
    y: (height - xAxisOffset) / 2,
    'font-size': '11px',
    transform: 'rotate(270, 10, 124)'
  });

  // xAxis

  svg.append('g').append('line').attrs({
    x1: yAxisOffset,
    y1: height - xAxisOffset,
    x2: width - statsBoxWidth,
    y2: height - xAxisOffset,
    stroke: theme.black
  });

  // Vertical line on the right of the protein bar

  svg.append('g').append('line').attrs({
    class: 'yAxisRight',
    x1: width - statsBoxWidth,
    y1: height - xAxisOffset,
    x2: width - statsBoxWidth,
    y2: height - xAxisOffset + proteinHeight,
    stroke: theme.black
  });

  // Horizontal line under protein bar

  svg.append('g').append('line').attrs({
    class: 'xAxisBottom',
    x1: yAxisOffset,
    y1: height - xAxisOffset + proteinHeight + halfPixel,
    x2: width - statsBoxWidth,
    y2: height - xAxisOffset + proteinHeight + halfPixel,
    stroke: theme.black
  });

  var _setupMinimap = setupMinimap({
    svg: svg,
    width: width,
    height: height,
    yAxisOffset: yAxisOffset,
    xAxisOffset: xAxisOffset,
    xAxisLength: xAxisLength,
    proteinHeight: proteinHeight,
    domainWidth: domainWidth,
    statsBoxWidth: statsBoxWidth,
    uniqueSelector: uniqueSelector
  }),
    minimapZoomArea = _setupMinimap.minimapZoomArea;

  chart.append('text').text(proteinDb).attrs({
    x: 5,
    y: height - xAxisOffset + 25,
    'font-size': '11px'
  });


  var _setupMutations = setupMutations({
    d3: d3,
    chart: chart,
    consequenceColors: consequenceColors,
    scaleLinearY: scaleLinearY,
    hasCustomMutationColor: hasCustomMutationColor,
    getMutationColor: getMutationColor,
    onMutationClick: onMutationClick,
    onMutationMouseover: onMutationMouseover,
    onMutationMouseout: onMutationMouseout,
    mutationId: mutationId,
    data: data,
    yAxisOffset: yAxisOffset,
    xAxisOffset: xAxisOffset,
    height: height,
    proteinHeight: proteinHeight,
    scale: scale,
    maxDonors: maxDonors,
    store: store,
    uniqueSelector: uniqueSelector
  }),
    mutationChartLines = _setupMutations.mutationChartLines,
    mutationChartCircles = _setupMutations.mutationChartCircles,
    selectedMutationBox = _setupMutations.selectedMutationBox;

  var otherLinesData = otherPositions;
  var _setupLines = setupLines({
    d3: d3,
    chart: chart,
    scaleLinearY: scaleLinearY,
    data: otherLinesData,
    yAxisOffset: yAxisOffset,
    xAxisOffset: xAxisOffset,
    height: height,
    proteinHeight: proteinHeight,
    scale: scale,
    uniqueSelector: uniqueSelector
  }),
    otherLines = _setupLines.otherLines,
    otherCircles = _setupLines.otherCircles;

 //current position on minimap
 chart.append('line').attrs({
  class: 'yAxis',
  x1: currentPosition * scale + yAxisOffset + halfPixel,
  y1: height - xAxisOffset + proteinHeight + 60,
  x2: currentPosition * scale + yAxisOffset + halfPixel,
  y2: height - xAxisOffset + proteinHeight + 20,
  stroke: theme.red
});

//current position on graph
var currentLine = chart.append('g').selectAll('line').data([{ x: currentPosition }]).enter().append('line').attrs({
  'clip-path': 'url(#' + uniqueSelector + '-chart-clip)',
  x1: function x1(d) {
    return d.x * scale + yAxisOffset + halfPixel;
  },
  y1: 0,
  x2: function x2(d) {
    return d.x * scale + yAxisOffset + halfPixel;
  },
  y2: height - xAxisOffset + proteinHeight,
  stroke: theme.red
});
  var minimapSlideTarget = svg.append('g').append('rect').attrs(Object.assign({
    class: 'minimap-slide-target',
    x: xAxisLength + yAxisOffset - 20,
    y: height - xAxisOffset + proteinHeight + 25
  }, dim(15, 15), {
      fill: 'rgb(255, 255, 255)',
      stroke: 'rgb(57, 57, 57)',
      cursor: 'move'
    }));

  var minimapSlideTargetArrow = svg.append('text').text('\u27FA').attrs({
    class: 'minimap-slide-target-arrow',
    x: xAxisLength + yAxisOffset - 19,
    y: height - xAxisOffset + proteinHeight + 36,
    'font-size': '11px',
    'pointer-events': 'none'
  });


  var _setupProteins = setupProteins({
    d3: d3,
    chart: chart,
    defs: defs,
    onProteinMouseover: onProteinMouseover,
    onProteinMouseout: onProteinMouseout,
    data: data,
    scale: scale,
    yAxisOffset: yAxisOffset,
    xAxisOffset: xAxisOffset,
    proteinHeight: proteinHeight,
    height: height,
    uniqueSelector: uniqueSelector
  }),
    proteinBars = _setupProteins.proteinBars,
    proteinClipPaths = _setupProteins.proteinClipPaths,
    proteinNames = _setupProteins.proteinNames;

  var _setupTicks = setupTicks({
    d3Root: d3Root,
    svg: svg,
    numYTicks: numYTicks,
    numXTicks: numXTicks,
    maxDonors: maxDonors,
    scaleLinearY: scaleLinearY,
    xAxisOffset: xAxisOffset,
    yAxisOffset: yAxisOffset,
    domainWidth: domainWidth,
    scale: scale,
    height: height
  }),
    xTicks = _setupTicks.xTicks,
    yTicks = _setupTicks.yTicks,
    yTicksLine = _setupTicks.yTicksLine;

  var _ref2 = hideStats ? {} : setupStats({
    d3: d3,
    d3Root: d3Root,
    consequenceColors: consequenceColors,
    data: data,
    store: store,
    selector: selector,
    hideStats: hideStats,
    statsBoxWidth: statsBoxWidth,
    width: width,
    consequences: consequences,
    impacts: impacts,
    mutationChartLines: mutationChartLines,
    mutationChartCircles: mutationChartCircles,
    selectedMutationBox: selectedMutationBox,
    height: height,
    xAxisOffset: xAxisOffset,
    root: root,
    yTicks: yTicks,
    yTicksLine: yTicksLine,
    logoElement: logoElement,
    proteinHeight:proteinHeight,
    otherCircles: otherCircles,
    otherLines: otherLines,
    otherLinesData: otherLinesData 
  }),
    stats = _ref2.stats,
    logo = _ref2.logo,
    consequencesCheckboxContainers = _ref2.consequencesCheckboxContainers,
    impactsCheckboxContainers = _ref2.impactsCheckboxContainers,
    mutationCount = _ref2.mutationCount;

  var draw = animator({
    d3: d3,
    d3Root: d3Root,
    store: store,
    data: data,
    yAxisOffset: yAxisOffset,
    xAxisOffset: xAxisOffset,
    statsBoxWidth: statsBoxWidth,
    height: height,
    width: width,
    domainWidth: domainWidth,
    scale: scale,
    proteinHeight: proteinHeight,
    numXTicks: numXTicks,
    mutationChartLines: mutationChartLines,
    mutationChartCircles: mutationChartCircles,
    selectedMutationBox: selectedMutationBox,
    consequences: consequences,
    impacts: impacts,
    consequenceColors: consequenceColors,
    consequencesCheckboxContainers: consequencesCheckboxContainers,
    impactsCheckboxContainers: impactsCheckboxContainers,
    proteinBars: proteinBars,
    proteinClipPaths: proteinClipPaths,
    proteinNames: proteinNames,
    xTicks: xTicks,
    minimapZoomArea: minimapZoomArea,
    minimapSlideTarget: minimapSlideTarget,
    minimapSlideTargetArrow: minimapSlideTargetArrow,
    mutationCount: mutationCount,
    yTicks: yTicks,
    yTicksLine: yTicksLine,
    animate: animate,
    hideStats: hideStats,
    currentLine: currentLine,
    otherLines: otherLines,
    otherCircles:otherCircles
  });

  proteinBars.on('click', function (d) {
    store.update({
      animating: true,
      targetMin: d.start,
      targetMax: d.end
    });
    draw();
  });

  var reset = function reset() {
    store.update({
      animating: animate,
      targetMin: 0,
      targetMax: domainWidth,
      consequenceFilters: [],
      impactFilters: []
    });

    if (!hideStats) {
      Object.values(consequencesCheckboxContainers).forEach(function (svg) {
        svg.select('.toggle-checkbox').attr('data-checked', 'true').html('\u2713');
      });

      updateStats({
        d3: d3,
        d3Root: d3Root,
        store: store,
        data: data,
        consequences: consequences,
        impacts: impacts,
        consequenceColors: consequenceColors,
        height: height,
        xAxisOffset: xAxisOffset,
        mutationChartLines: mutationChartLines,
        mutationChartCircles: mutationChartCircles,
        selectedMutationBox: selectedMutationBox,
        consequencesCheckboxContainers: consequencesCheckboxContainers,
        impactsCheckboxContainers: impactsCheckboxContainers,
        mutationCount: mutationCount,
        yTicks: yTicks,
        yTicksLine: yTicksLine,
        otherLines: otherLines,
        otherCircles:otherCircles
      });
    }

    updateMutations({ d3Root: d3Root, checked: true, data: data, mutationClass: null, type: null });
    updateLines({ d3Root: d3Root, checked: true, data: data, mutationClass: null, type: null });
    draw();
  };

  var _setupZoomHandlers = setupZoomHandlers({
    d3: d3,
    d3Root: d3Root,
    root: root,
    store: store,
    yAxisOffset: yAxisOffset,
    xAxisOffset: xAxisOffset,
    xAxisLength: xAxisLength,
    domainWidth: domainWidth,
    scale: scale,
    svg: svg,
    height: height,
    proteinHeight: proteinHeight,
    draw: draw,
    uniqueSelector: uniqueSelector
  }),
    removeZoomHandlers = _setupZoomHandlers.removeZoomHandlers;

  var remove = function remove() {
    removeZoomHandlers();
    svg.remove();
    if (!hideStats) {
      if (logo) logo.remove();
      if (stats) stats.remove();
    }
  };

  var update = function update(props) {
    store.update(props);
    draw();
  };

  draw();
  onInit();

  return {
    reset: reset,
    updateStats: updateStats,
    draw: draw,
    remove: remove,
    store: store,
    update: update
  };
};

/*----------------------------------------------------------------------------*/

export default proteinLolliplot;
export { setupStore };