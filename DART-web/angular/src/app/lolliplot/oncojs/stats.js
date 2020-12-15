import startCase from 'lodash.startcase';
import countBy from 'lodash.countby';
import { updateMutations } from './mutations';
import { updateLines } from './lines';
import theme from './theme';
import { animateScaleY } from './animator';

// TODO: place in theme?

var renderCheckboxItem = function renderCheckboxItem(_ref) {
  var colors = _ref.colors,
      type = _ref.type,
      dataSource = _ref.dataSource;
  return '\n  <span\n    class="toggle-checkbox"\n    data-checked="true"\n    style="color: ' + (colors[type] || colors.default) + ';\n    text-align: center;\n    border: 2px solid ' + (colors[type] || colors.default) + ';\n    display: inline-block;\n    width: 23px;\n    cursor: pointer;\n    margin-right: 6px;"\n  >\n    \u2713\n  </span>\n  <span>' + startCase(type) + ':</span>\n  <span style="margin: 0 10px 0 auto;" class="counts">\n    <b>' + dataSource[type].length + '</b> / <b>' + dataSource[type].length + '</b>\n  </span>\n';
};

var setupStats = function setupStats(_ref2) {
  var d3 = _ref2.d3,
      d3Root = _ref2.d3Root,
      consequenceColors = _ref2.consequenceColors,
      data = _ref2.data,
      store = _ref2.store,
      hideStats = _ref2.hideStats,
      statsBoxWidth = _ref2.statsBoxWidth,
      width = _ref2.width,
      consequences = _ref2.consequences,
      impacts = _ref2.impacts,
      mutationChartLines = _ref2.mutationChartLines,
      mutationChartCircles = _ref2.mutationChartCircles,
      selectedMutationBox = _ref2.selectedMutationBox,
      height = _ref2.height,
      xAxisOffset = _ref2.xAxisOffset,
      yTicks = _ref2.yTicks,
      yTicksLine = _ref2.yTicksLine,
      logoElement = _ref2.logoElement,
      proteinHeight = _ref2.proteinHeight,
      otherLinesData = _ref2.otherLinesData,
      otherLines = _ref2.otherLines,
      otherCircles = _ref2.otherCircles;

  // Stats Bar

  var padding = 35;

  var stats = d3Root.append('div').attr('id', 'mutation-stats').style('display', hideStats ? 'none' : 'block').style('background-color', 'white').style('border', '1px solid rgb(186, 186, 186)').style('padding', '13px').style('width', statsBoxWidth - padding + 'px');

  var mutationCount = stats.style('position', 'absolute').style('top', '0px').style('left', width - statsBoxWidth + padding + 'px').style('line-height', '20px').append('div').html('Viewing <b>' + data.mutations.length + '</b> / <b>' + data.mutations.length + '</b> Mutations').attr('class', 'mutation-count').style('font-size', '16px');

  stats.append('label').attr('for', 'filter-type').text('select filter type').style('display', 'none');

  d3Root.selectAll('[class^=mutation-circle]').attr('fill', function (d) {
    return theme.impactsColors[d.impact] || theme.impactsColors.default;
  });

  stats.append('div').text('Click to filter mutations').style('margin-top', '6px').style('font-size', '11px').style('color', theme.black);

  var impactsContainer = stats.append('span').text('Impact (VEP):').attr('id', 'class-Impact').style('display', 'block').style('margin-top', '6px').style('font-size', '14px');

  var impactsCheckboxContainers = Object.keys(impacts).reduce(function (acc, type) {
    var _Object$assign2;

    return Object.assign({}, acc, (_Object$assign2 = {}, _Object$assign2[type] = impactsContainer.append('div').html(renderCheckboxItem({
      colors: theme.impactsColors,
      type: type,
      dataSource: impacts
    })).style('margin-top', '6px').style('font-size', '14px').style('display', 'flex').style('align-items', 'center').on('click', function () {
      d3.event.target.dataset.checked = d3.event.target.dataset.checked === 'true' ? 'false' : 'true';

      var checked = d3.event.target.dataset.checked === 'true';

      var _store$getState3 = store.getState(),
          impactFilters = _store$getState3.impactFilters;

      d3.select(this).select('.toggle-checkbox').html(checked ? '\u2713' : '&nbsp;');

      store.update({ impactFilters: checked ? impactFilters.filter(function (d) {
          return d !== type;
        }) : [].concat(impactFilters, [type])
      });

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
        impactsCheckboxContainers: impactsCheckboxContainers,
        mutationCount: mutationCount,
        yTicks: yTicks,
        yTicksLine: yTicksLine,
        otherCircles : otherCircles,
        otherLines : otherLines
      });

      updateMutations({ d3Root: d3Root, checked: checked, mutationClass: 'impact', type: type, data: data });
      updateLines({ d3Root: d3Root, checked: checked, mutationClass: 'impact', type: type, data: otherLinesData });
    }), _Object$assign2));
  }, {});

  var logo;
  if (logoElement){
    logo = d3Root.append('div').style('display', 'block').style('background-color', 'white').style('border', '1px solid rgb(186, 186, 186)').style('padding', '13px').style('width', statsBoxWidth - padding + 'px');
    logo.style('position','absolute').style('top',height-xAxisOffset+proteinHeight+'px').style('left', width - statsBoxWidth + padding + 'px');
    logo.style('height', '90px');  
    logo.append('div').style('line-height', '20px').append('span').text('Powered by: ');
    logo.append('div').style('height','calc(100% - 20px)').html(logoElement);    
  }

  return {
    stats: stats,
    logo: logo,
    impactsCheckboxContainers: impactsCheckboxContainers,
    mutationCount: mutationCount
  };
};

var updateStats = function updateStats(_ref3) {
  var d3 = _ref3.d3,
      d3Root = _ref3.d3Root,
      store = _ref3.store,
      data = _ref3.data,
      consequences = _ref3.consequences,
      impacts = _ref3.impacts,
      mutationChartLines = _ref3.mutationChartLines,
      mutationChartCircles = _ref3.mutationChartCircles,
      selectedMutationBox = _ref3.selectedMutationBox,
      height = _ref3.height,
      xAxisOffset = _ref3.xAxisOffset,
      impactsCheckboxContainers = _ref3.impactsCheckboxContainers,
      mutationCount = _ref3.mutationCount,
      yTicks = _ref3.yTicks,
      yTicksLine = _ref3.yTicksLine,
      otherCircles = _ref3.otherCircles,
      otherLines = _ref3.otherLines;

  var _store$getState4 = store.getState(),
      min = _store$getState4.min,
      max = _store$getState4.max,
      consequenceFilters = _store$getState4.consequenceFilters,
      impactFilters = _store$getState4.impactFilters,
      animating = _store$getState4.animating,
      type = _store$getState4.type;

  var visibleMutations = data.mutations.filter(function (d) {
    return d.x > min && d.x < max && (type === 'Consequence' && !consequenceFilters.includes(d.consequence) || type === 'Impact' && !impactFilters.includes(d.impact));
  });

  var visibleMutationCounts = {
    impacts: countBy(visibleMutations, 'impact'),
    consequences: countBy(visibleMutations, 'consequence')
  };


  Object.entries(impactsCheckboxContainers).forEach(function (_ref5) {
    var type = _ref5[0],
        container = _ref5[1];
    return container.select('.counts').html('<b>' + (visibleMutationCounts.impacts[type] || 0) + '</b> / <b>' + impacts[type].length + '</b>');
  });

  mutationCount.html('Viewing <b>' + visibleMutations.length + '</b> / <b>' + data.mutations.length + '</b> Mutations');

  if (!animating) {
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
      visibleMutations: visibleMutations,
      yTicks: yTicks,
      yTicksLine: yTicksLine
    });
  }
};

/*----------------------------------------------------------------------------*/

export { setupStats, updateStats };