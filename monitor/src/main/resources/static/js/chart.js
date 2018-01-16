default_option = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            label: {
                backgroundColor: '#6a7985'
            }
        }

    },
    legend: {
        left: 'left'
    },
    xAxis: {
        type: 'category',

        splitLine: {show: false},
        boundaryGap: false
    },
    yAxis: {
        type: 'value',

        boundaryGap: [0, '200%']
    }

};