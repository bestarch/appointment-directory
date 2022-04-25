  
  
  const data = {
    labels: dates,
    datasets: [{
      label: 'Appointments over time',
      backgroundColor: 'rgb(255, 99, 132)',
      borderColor: 'rgb(255, 99, 132)',
      data: count,
    }]
  };

  const config = {
    type: 'bar',
    data: data,
    options: {
        scales: {
            x: {
                type: 'Days'
            }
        }
    }
  };
