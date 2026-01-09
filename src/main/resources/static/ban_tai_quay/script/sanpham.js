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
         $scope.newSanPham = {
             ten: '',
             trangThai: 1,
             theLoai: '',
             chatLieu: '',
             thuongHieu: '',
             moTa: '',
             sizeIds: [],
             mauIds: [],
             ctspList: []
         };

         $scope.errorMessage = '';
         $scope.successMessage = '';

        function resetNewSanPham() {
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
        }

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

            $scope.showError = function (msg) {
                $scope.errorMessage = msg;
                setTimeout(() => {
                    document.querySelector('.alert-danger')?.scrollIntoView({ behavior: 'smooth' });
                }, 50);
            };

           $scope.addSanPham = function () {
            $scope.errorMessage = '';
            $scope.successMessage = '';

               if (!$scope.newSanPham.ten || !$scope.newSanPham.ten.trim()) {
                   $scope.errorMessage = "Vui lòng nhập tên sản phẩm";
                   return;
               }

               if ($scope.newSanPham.trangThai === undefined || $scope.newSanPham.trangThai === null) {
                   $scope.errorMessage = "Vui lòng chọn trạng thái";
                   return;
               }

               if (!$scope.newSanPham.theLoai) {
                   $scope.errorMessage = "Vui lòng chọn thể loại";
                   return;
               }

               if (!$scope.newSanPham.chatLieu) {
                   $scope.errorMessage = "Vui lòng chọn chất liệu";
                   return;
               }

               if (!$scope.newSanPham.thuongHieu) {
                   $scope.errorMessage = "Vui lòng chọn thương hiệu";
                   return;
               }
                if ($scope.newSanPham.sizeIds.length === 0) {
                    $scope.errorMessage = "Vui lòng chọn ít nhất 1 size";
                    return;
                }
                if ($scope.newSanPham.mauIds.length === 0) {
                    $scope.errorMessage = "Vui lòng chọn ít nhất 1 màu sắc";
                    return;
                }

               for (let i = 0; i < $scope.newSanPham.ctspList.length; i++) {
                   let ct = $scope.newSanPham.ctspList[i];

                   if (!ct.gia || ct.gia <= 0) {
                       $scope.errorMessage = `Giá không hợp lệ tại dòng ${i + 1}`;
                       return;
                   }

                   if (ct.soLuong === undefined || ct.soLuong < 0) {
                       $scope.errorMessage = `Số lượng không hợp lệ tại dòng ${i + 1}`;
                       return;
                   }

                   if (!ct.file) {
                       $scope.errorMessage = `Vui lòng chọn ảnh tại dòng ${i + 1}`;
                       return;
                   }
               }
               if ($scope.tenTrung) {
                     return;
               }
               submitSanPham();
           };
           $scope.tenTrung = false;
            $scope.checkTenSanPham = function () {
                if (!$scope.newSanPham.ten || !$scope.newSanPham.ten.trim()) {
                    $scope.tenTrung = false;
                    return;
                }

                $http.get("http://localhost:8084/san-pham/check-ten", {
                    params: { ten: $scope.newSanPham.ten }
                }).then(function (res) {
                    $scope.tenTrung = res.data === true;
                }).catch(function () {
                    $scope.tenTrung = false;
                });
            };

            function submitSanPham() {
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
                    formData.append(`ctspList[${index}].image`, ct.file);
                });

                $http.post("http://localhost:8084/san-pham/add", formData, {
                    transformRequest: angular.identity,
                    headers: { 'Content-Type': undefined }
                }).then(function (res) {
                    $scope.successMessage = res.data.message || "Thêm sản phẩm thành công 🎉";
                    $('#addModal').modal('hide');
                    resetNewSanPham();
                    $scope.loadSanPham();
                }).catch(function () {
                    $scope.errorMessage = "Thêm thất bại";
                });
            }


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
                $scope.errorMessage = '';
                  $scope.successMessage = '';
                 resetNewSanPham();

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
               ct.file = file;
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
