  const labels = [
    'Apr 21',
    'Apr 22',
    'Apr 23',
    'Apr 24',
    'Apr 25',
    'Apr 26',
    'Apr 27',
    'Apr 28',
    'Apr 29',
    'Apr 30',
    'May 1',
    'May 2',
    'May 3',
    'May 4'
  ];

  const data = {
    labels: labels,
    datasets: [{
      label: 'Appointments over time',
      backgroundColor: 'rgb(255, 99, 132)',
      borderColor: 'rgb(255, 99, 132)',
      data: [0, 10, 5, 2, 20, 30, 45, 3, 6,2,0,1,4,10],
    }]
  };

  const config = {
    type: 'bar',
    data: data,
    options: {}
  };
