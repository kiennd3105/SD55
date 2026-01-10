    app.controller("trangChuCtrl", function ($scope, $http,$timeout) {

       const BASE = "http://localhost:8084/api/thong-ke";
           $scope.tongDoanhThu = 0;
           $scope.tongDon = 0;
           $scope.dsDoanhThu = [];
           $scope.dsTopSP = [];
           let chart = null;
           $scope.filter = {};


           function setDefaultDate() {
               const today = new Date();
               const from = new Date();
               from.setDate(today.getDate() - 4);

               $scope.filter.fromDate = from;   // ✅ Date
               $scope.filter.toDate = today;    // ✅ Date
           }

        function formatDate(d) {
            if (!d) return null;
            if (typeof d === 'string') return d;
            return d.toISOString().substring(0, 10);
        }


          $scope.loadDoanhThu = function () {
              $http.get(BASE + "/doanh-thu-ngay", {
                  params: {
                   fromDate: formatDate($scope.filter.fromDate),
                   toDate: formatDate($scope.filter.toDate)
                  }
              }).then(function (res) {

                  let labels = [];
                  let data = [];

                  $scope.tongDoanhThu = 0;
                  $scope.tongDon = 0;

                  res.data.forEach(d => {
                      labels.push(d.thoiGian);
                      data.push(d.doanhThu);
                      $scope.tongDoanhThu += d.doanhThu;
                      $scope.tongDon += d.tongDon;
                  });

                  if (chart) chart.destroy();

                  chart = new Chart(
                      document.getElementById("doanhThuChart"),
                      {
                          type: 'bar',
                          data: {
                              labels: labels,
                              datasets: [{
                                  label: 'Doanh thu',
                                  data: data
                              }]
                          },
                          options: {
                              responsive: true,
                              plugins: {
                                  legend: { display: false }
                              }
                          }
                      }
                  );
              }).catch(err => console.error("Lỗi doanh thu", err));
          };

              // ===== TOP SẢN PHẨM =====
              $scope.loadTopSanPham = function () {
                  $http.get(BASE + "/top-san-pham")
                      .then(function (res) {
                          $scope.dsTopSP = res.data;
                      })
                      .catch(function (err) {
                          console.error("Lỗi top SP", err);
                      });
              };
             $scope.locThongKe = function () {
                 $scope.loadDoanhThu();
                 $scope.loadTopSanPham();
             };

           setDefaultDate();
           $scope.loadDoanhThu();
           $scope.loadTopSanPham();
    });
