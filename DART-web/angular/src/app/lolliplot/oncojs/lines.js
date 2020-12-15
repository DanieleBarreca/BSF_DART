import { halfPixel } from './spatial';
import theme from './theme';

var setupLines = function setupLines(_ref) {
  var d3 = _ref.d3,
    data = _ref.data,
      yAxisOffset = _ref.yAxisOffset,
      xAxisOffset = _ref.xAxisOffset,
      height = _ref.height,
      proteinHeight = _ref.proteinHeight,
      scale = _ref.scale,
      chart = _ref.chart,
      uniqueSelector = _ref.uniqueSelector;

      var otherLines = chart.append('g').selectAll('line').data(data).enter().append('line').attrs({
        class: function _class(d) {
            return 'other-line-' + d.id;
        },
        'clip-path': 'url(#' + uniqueSelector + '-chart-clip)',
        x1: function x1(d) {
          return d.x * scale + yAxisOffset + halfPixel;
        },
        y1: 0,
        x2: function x2(d) {
          return d.x * scale + yAxisOffset + halfPixel;
        },
        y2: height - xAxisOffset + proteinHeight,
        stroke: theme.blue
      });
    
      var otherCircles = chart.append('g').selectAll('rect').data(data).enter().append('rect').attrs({    
        class: function _class(d) {
            return 'other-circle-' + d.id;
        },
        'clip-path': 'url(#' + uniqueSelector + '-chart-clip)',
        x: function cx(d) {
          return d.x * scale + yAxisOffset -theme.mutationRadius+ halfPixel;
        },
        y: 0,
        width: theme.mutationRadius*2,
        height: theme.mutationRadius*2,
        fill: function fill(d) {
          return theme.impactsColors[d.impact] || theme.impactsColors.default;
        }
      })
    

  return { otherLines: otherLines, otherCircles: otherCircles};
};

var updateLines = function (_ref2) {
  var d3Root = _ref2.d3Root,
      checked = _ref2.checked,
      mutationClass = _ref2.mutationClass,
      type = _ref2.type,
      data = _ref2.data;

  var selectedMutations = mutationClass ? data.filter(function (x) {
    return x[mutationClass] === type;
  }) : data.slice();

  if (!checked) {
    selectedMutations.forEach(function (d) {
      d3Root.select('.other-line-' + d.id).attr('opacity', 0);
      d3Root.selectAll('.other-circle-' + d.id).attr('opacity', 0);
    });
  } else {
    selectedMutations.forEach(function (d) {
      d3Root.select('.other-line-' + d.id).attr('opacity', 1);
      d3Root.selectAll('.other-circle-' + d.id).attr('opacity', 1);
    });
  }
};

export { setupLines, updateLines };