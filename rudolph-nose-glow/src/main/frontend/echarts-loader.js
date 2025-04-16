import * as echarts from 'https://cdn.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js';

window.renderFlowChart = function () {
    const chartDom = document.getElementById('flowChart');
    const myChart = echarts.init(chartDom);
    const option = {
        title: { text: 'Total Flow (Last 24 Hours)', left: 'center' },
        tooltip: { trigger: 'axis', formatter: '{b} : {c} l/h' },
        xAxis: {
            type: 'category',
            data: ['13:00', '16:00', '19:00', '22:00', '01:00', '04:00', '07:00', '10:00']
        },
        yAxis: {
            type: 'value',
            name: 'Flow total (l/h)',
            min: 0,
            max: 1.25
        },
        series: [{
            name: 'Irrigation 1',
            data: [0.2, 0.5, 0.3, 0.6, 0.4, 0.7, 0.8, 1.0],
            type: 'line',
            smooth: true,
            symbol: 'circle'
        }]
    };
    myChart.setOption(option);
};

window.renderSpeedGauge = function () {
    const chartDom = document.getElementById('speedGauge');
    const myChart = echarts.init(chartDom);
    const option = {
        title: { text: 'Harvest Robot Speed', left: 'center' },
        series: [{
            type: 'gauge',
            progress: { show: true },
            detail: { valueAnimation: true, formatter: '{value} km/h' },
            data: [{ value: 2, name: 'Speed' }],
            min: 0,
            max: 4
        }]
    };
    myChart.setOption(option);
};