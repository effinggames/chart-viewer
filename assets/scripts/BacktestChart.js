import * as angular from 'angular';
import * as Highcharts from 'highcharts';

angular.module('app')
    .directive('backtestChart', function() {
        return {
            restrict: 'A',
            scope: {
                chartData: '&'
            },
            link: function ($scope, $elem, $attrs) {
                console.log("chartData", $scope.chartData());
                const algoResults = $scope.chartData().algoResults;
                const series = algoResults.map(function(algoResult) {
                    const startingValue = algoResult.historicalValues[0];
                    const data = algoResult.historicalValues.map(function(value, index) {
                        return [new Date(algoResult.historicalDates[index]).getTime(), parseFloat((value / startingValue).toFixed(4))]
                    });
                    return {
                        name: algoResult.algoName,
                        data: data
                    }
                });
                const subtitle = algoResults.map(i => i.algoName).join(", ");

                Highcharts.chart($elem[0], {
                    chart: {
                        alignTicks: false,
                        type: 'spline'
                    },
                    title: {
                        text: 'Backtest Results'
                    },
                    subtitle: {
                        text: subtitle
                    },
                    xAxis: {
                        type: 'datetime',
                        dateTimeLabelFormats: {
                            year: '%Y'
                        },
                        title: {
                            text: 'Date'
                        },
                        tickInterval: Date.UTC(2010, 1, 2) - Date.UTC(2010, 1, 1)
                    },
                    yAxis: {
                        title: {
                            text: 'Returns'
                        },
                        type: 'logarithmic'
                    },
                    tooltip: {
                        snap: 0,
                        shared: true,
                        crosshairs: true,
                        followPointer: true,
                        dateTimeLabelFormats: {
                            month:"%Y-%m-%d",
                            day:"%Y-%m-%d",
                        },
                        positioner: function(labelWidth, labelHeight, point) {
                            const chart = this.chart;
                            let tooltipX;
                            let tooltipY;

                            if (point.plotX - labelWidth < 0) {
                                tooltipX = point.plotX + chart.plotLeft + 20; //right side
                            } else {
                                tooltipX = point.plotX + chart.plotLeft - labelWidth - 20; //left side
                            }
                            tooltipY = Math.max(point.plotY + chart.plotTop - 20, chart.plotTop);
                            return {
                                x: tooltipX,
                                y: tooltipY
                            };
                        }
                    },
                    legend: {
                        layout: 'vertical',
                        align: 'left',
                        x: 120,
                        verticalAlign: 'top',
                        y: 40,
                        floating: true,
                        backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
                    },
                    plotOptions: {
                        series: {
                            lineWidth: 2.5,
                        },
                        spline: {
                            marker: {
                                enabled: false
                            }
                        }
                    },
                    series: series,
                    credits: {
                        enabled: false
                    }
                });
            }
        };
    });
