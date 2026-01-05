    app.directive("fileChange", function () {
        return {
            scope: {
                fileChange: "&"
            },
            link: function (scope, element) {
                element.on("change", function (event) {
                    scope.$apply(function () {
                        scope.fileChange({ files: event.target.files });
                    });
                });
            }
        };
    });

    app.controller("sanPhamCtrl", function ($scope, $http,$routeParams,$timeout) {
         $scope.dsSanPham = [];
         $scope.sanPhamDetail = {};
         $scope.dsSPCT = [];
         $scope.dsTheLoai = [];
         $scope.dsChatLieu = [];
         $scope.dsThuongHieu = [];
         $scope.dsSize = [];
         $scope.dsMauSac = [];
        $http.get("http://localhost:8084/the-loai/getAll")
             .then(function (res) {
                    console.log("DS THỂ LOẠI:", res.data);
                    $scope.dsTheLoai = res.data;
                });

        $http.get("http://localhost:8084/san-pham/chat-lieu")
             .then(function (res) {
                    console.log("DS chất liệu:", res.data);
                    $scope.dsChatLieu = res.data;
                });

        $http.get("http://localhost:8084/san-pham/thuong-hieu")
             .then(function (res) {
                    console.log("DS thương hiệu:", res.data);
                    $scope.dsThuongHieu = res.data;
                });

        $http.get("http://localhost:8084/san-pham/size")
             .then(function (res) {
                    console.log("DS size:", res.data);
                    $scope.dsSize = res.data;
                });

        $http.get("http://localhost:8084/san-pham/mau-sac")
             .then(function (res) {
                    console.log("DS Mauf :", res.data);
                    $scope.dsMauSac = res.data;
                });

       $scope.loadSanPham = function () {
           $http.get("http://localhost:8084/san-pham/getAll")
               .then(function (response) {
                   $scope.dsSanPham = response.data;
                   console.log("Reload sản phẩm:", response.data);
               })
               .catch(function (error) {
                   console.error("Lỗi load sản phẩm", error);
               });
       };


        $scope.openDetail = function (id) {
               $http.get("http://localhost:8084/san-pham/detail/" + id)
                   .then(function (res) {
                       console.log("DETAIL từ API:", res.data);
                       $scope.sanPhamDetail = res.data;
                       $timeout(function () {
                           var modal = new bootstrap.Modal(
                               document.getElementById('detailModal')
                           );
                           modal.show();
                       });
                   });
           };


            $scope.openCTSP = function (idSanPham) {

                $http.get("http://localhost:8084/san-pham/san-pham/" + idSanPham)
                    .then(function (res) {

                        console.log("DS CTSP:", res.data);
                        $scope.dsSPCT = res.data;

                        // mở modal CTSP
                        $timeout(function () {
                            var modal = new bootstrap.Modal(
                                document.getElementById('ctspModal')
                            );
                            modal.show();
                        });
                    })
                    .catch(function (err) {
                        console.error("Lỗi load CTSP", err);
                    });
            };


           $scope.addSanPham = function () {

               if (!$scope.newSanPham.ctspList || $scope.newSanPham.ctspList.length === 0) {
                   alert("Vui lòng chọn Size và Màu sắc");
                   return;
               }
               let formData = new FormData();
               formData.append("ten", $scope.newSanPham.ten);
               formData.append("trangThai", $scope.newSanPham.trangThai);
               formData.append("moTa", $scope.newSanPham.moTa);
               formData.append("theLoaiId", $scope.newSanPham.theLoai);
               formData.append("chatLieuId", $scope.newSanPham.chatLieu);
               formData.append("thuongHieuId", $scope.newSanPham.thuongHieu);

               $scope.newSanPham.ctspList.forEach((ct, index) => {
                   formData.append(`ctspList[${index}].sizeId`, ct.sizeId);
                   formData.append(`ctspList[${index}].mauId`, ct.mauId);
                   formData.append(`ctspList[${index}].gia`, ct.gia);
                   formData.append(`ctspList[${index}].soLuong`, ct.soLuong);

                   if (ct.file) {
                       formData.append(`ctspList[${index}].image`, ct.file);
                   }
               });

               $http.post("http://localhost:8084/san-pham/add", formData, {
                   transformRequest: angular.identity,
                   headers: { 'Content-Type': undefined }
               })
               .then(function (res) {
                    alert(res.data.message);
                   $('#addModal').modal('hide');
                   $scope.loadSanPham();
               })
               .catch(function (err) {
                   console.error(err);
                   alert("Có lỗi upload dữ liệu");
               });
           };


            $scope.generateCTSP = function () {

                $scope.newSanPham.ctspList = [];

                angular.forEach($scope.newSanPham.sizeIds, function (sizeId) {
                    angular.forEach($scope.newSanPham.mauIds, function (mauId) {

                        let size = $scope.dsSize.find(s => s.id === sizeId);
                        let mau = $scope.dsMauSac.find(m => m.id === mauId);

                        $scope.newSanPham.ctspList.push({
                            sizeId: sizeId,
                            mauId: mauId,
                            tenSize: size.tenSZ,
                            tenMau: mau.tenM,
                            gia: 0,
                            soLuong: 0,
                            file: null,
                            preview: null
                        });
                    });
                });
            };
            $scope.toggleSize = function (id) {
                let idx = $scope.newSanPham.sizeIds.indexOf(id);
                idx > -1
                    ? $scope.newSanPham.sizeIds.splice(idx, 1)
                    : $scope.newSanPham.sizeIds.push(id);

                $scope.generateCTSP();
            };

            $scope.toggleMau = function (id) {
                let idx = $scope.newSanPham.mauIds.indexOf(id);
                idx > -1
                    ? $scope.newSanPham.mauIds.splice(idx, 1)
                    : $scope.newSanPham.mauIds.push(id);

                $scope.generateCTSP();
            };


            $scope.newSanPham = {
                ten: "",
                trangThai: 1,
                moTa: "",
                theLoai: "",
                chatLieu: "",
                thuongHieu: "",
                sizeIds: [],
                mauIds: [],
                ctspList: []
            };

            $scope.openAddModal = function () {
                $scope.errorMessage = null;

                $scope.newSanPham = {
                    ten: "",
                    soLuong: "",
                    trangThai: 1,
                    moTa: "",
                    theLoai: "",
                    chatLieu: "",
                    thuongHieu: "",
                     sizeIds: [],
                     mauIds: [],
                     ctspList: []
                };

                $timeout(function () {
                    var modal = new bootstrap.Modal(
                        document.getElementById('addModal')
                    );
                    modal.show();
                });
            };
           $scope.onSelectImage = function (files, ct) {

               let file = files[0];
               if (!file) return;

               // lưu file gửi BE
               ct.file = file;

               // preview ảnh
               let reader = new FileReader();
               reader.onload = function (e) {
                   $scope.$apply(function () {
                       ct.preview = e.target.result;
                   });
               };
               reader.readAsDataURL(file);
           };







        $scope.loadSanPham();


    });
