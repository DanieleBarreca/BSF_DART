import { dim, halfPixel } from './spatial';
import theme from './theme';

var setupMinimap = function setupMinimap(props) {
  
  props.svg.append('g').append('rect').attrs(Object.assign({
    class: 'minimap',
    x: props.yAxisOffset,
    y: props.height - props.xAxisOffset + props.proteinHeight + 20
  }, dim(props.xAxisLength, 50), {
    stroke: 'rgb(138, 138, 138)',
    fill: 'rgb(212, 212, 212)',
    cursor: 'text'
  }));

  props.svg.append('g').append('clipPath').attr('id', props.uniqueSelector + '-minimap-clip').append('rect').attrs(Object.assign({
    x: props.yAxisOffset,
    y: props.height - props.xAxisOffset + props.proteinHeight + 20
  }, dim(props.xAxisLength, 50)));

  var minimapZoomArea = props.svg.append('g').append('rect').attrs(Object.assign({
    class: 'minimap-zoom-area',
    x: props.yAxisOffset + halfPixel,
    y: props.height - props.xAxisOffset + props.proteinHeight + 20 + halfPixel
  }, dim(props.xAxisLength - 1, 50 - 1), {
    fill: 'rgb(255, 255, 255)',
    'pointer-events': 'none'
  }));

  props.svg.append('g').append('text').text('\n      Three ways to zoom in or out:\n      1) Click on a domain above.\n      2) Click and drag over the main chart above.\n      3) Click and drag over the gene map below.\n    ').attrs({
    class: 'minimap-label',
    x: props.yAxisOffset,
    y: props.height - props.xAxisOffset + props.proteinHeight + 15,
    'font-size': '11px'
  });

  props.svg.append('g').append('line').attrs({
    class: 'minimap-protein-mutation-divider',
    x1: props.yAxisOffset,
    y1: props.height - props.xAxisOffset + props.proteinHeight + 60 - halfPixel,
    x2: props.xAxisLength + props.yAxisOffset,
    y2: props.height - props.xAxisOffset + props.proteinHeight + 60 - halfPixel,
    stroke: theme.black
  });

  props.svg.append('g').append('text').text('aa 1').attrs({
    x: props.yAxisOffset,
    y: props.height - props.xAxisOffset + props.proteinHeight + 90,
    'font-size': '11px',
    'text-anchor': 'start'
  });

  props.svg.append('g').append('text').text('aa ' + props.domainWidth).attrs({
    x: props.width - props.statsBoxWidth,
    y: props.height - props.xAxisOffset + props.proteinHeight + 90,
    'font-size': '11px',
    'text-anchor': 'end'
  });

  props.svg.append('g').append('text').text('This track represents the whole gene. The white area is the current zoom level.').attrs({
    x: (props.width - props.statsBoxWidth) / 2,
    y: props.height - props.xAxisOffset + props.proteinHeight + 90,
    'font-size': '11px',
    'text-anchor': 'middle'
  });

  return {
    minimapZoomArea: minimapZoomArea
  };
};

/*----------------------------------------------------------------------------*/

export default setupMinimap;