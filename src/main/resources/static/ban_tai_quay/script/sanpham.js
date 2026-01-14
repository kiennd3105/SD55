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
         $scope.getSTT = function (i) { return i + 1; };
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
          $scope.showAddCTSP = false;
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
     $scope.customFilter = function(sp) {
         if ($scope.searchText) {
             let txt = $scope.searchText.toLowerCase();
             let ten = sp.ten ? sp.ten.toLowerCase() : '';
             let ma = sp.ma ? sp.ma.toLowerCase() : '';
             if (!(ten.includes(txt) || ma.includes(txt))) {
                 return false;
             }
         }
         return true;
     };


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



          $scope.openUpdate = function (id) {
              $http.get("http://localhost:8084/san-pham/detail/" + id)
                  .then(function (res) {
                      let sp = res.data;
                        console.log("dssp",res.data)
                      $scope.editSanPham = {
                          id: sp.id,
                          ten: sp.ten,
                          trangThai: sp.trangThai,
                          moTa: sp.moTa,
                          theLoaiId: sp.theLoaiId,
                          chatLieuId: sp.chatLieuId,
                          thuongHieuId: sp.thuongHieuId
                      };
                      $timeout(function () {
                          new bootstrap.Modal(
                              document.getElementById('editModal')
                          ).show();
                      });
                  })
                  .catch(function () {
                      alert("Không lấy được dữ liệu sản phẩm");
                  });
          };
           $scope.openCTSP = function (idSanPham) {
               $scope.currentSanPhamId = idSanPham;
               $scope.showAddCTSP = false;

               $scope.newCTSP = {
                   sanPhamId: idSanPham,
                   sizeId: "",
                   mauId: "",
                   gia: "",
                   soLuong: "",
                   file: null,
                   preview: null
               };

               $http.get("http://localhost:8084/san-pham/san-pham/" + idSanPham)
                   .then(res => {
                       $scope.dsSPCT = res.data;
                       console.log("ctsp",res.data)
                       $timeout(() => {
                           new bootstrap.Modal(
                               document.getElementById("ctspModal")
                           ).show();
                       });
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
                    setTimeout(() => {
                        $scope.$apply(() => {
                             $scope.successMessage = "";
                        });
                    }, 3000);
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
            $scope.updateSanPham = function () {
                if (!$scope.editSanPham.ten || !$scope.editSanPham.ten.trim()) {
                    $scope.errorMessage = "Tên sản phẩm không được để trống";
                    return;
                }
                let payload = {
                    ten: $scope.editSanPham.ten,
                    trangThai: $scope.editSanPham.trangThai,
                    moTa: $scope.editSanPham.moTa,
                    theLoaiId: $scope.editSanPham.theLoaiId,
                    chatLieuId: $scope.editSanPham.chatLieuId,
                    thuongHieuId: $scope.editSanPham.thuongHieuId
                };
                $http.put(
                    "http://localhost:8084/san-pham/update/" + $scope.editSanPham.id,
                    payload
                ).then(function (res) {
                    $scope.successMessage = res.data.message || "Cập nhật thành công";
                   setTimeout(() => {
                       $scope.$apply(() => {
                           $scope.successMessage = "";
                       });
                   }, 3000);
                    $('#editModal').modal('hide');
                    $scope.loadSanPham();
                }).catch(function () {
                    $scope.errorMessage = "Cập nhật thất bại";
                });
            };
            $scope.newCTSP = {
                sanPhamId: null,
                sizeId: "",
                mauId: "",
                gia: "",
                soLuong: "",
                file: null,
                preview: null
            };

            $scope.ctspError = "";
            $scope.ctspSuccess = "";
            $scope.openAddCTSP = function (sanPhamId) {
                $scope.showAddCTSP = true;
                $scope.isEditCTSP = false;
                $scope.ctspError = "";
                $scope.ctspSuccess = "";
                $scope.newCTSP = {
                    sanPhamId: sanPhamId,
                    sizeId: "",
                    mauId: "",
                    gia: "",
                    soLuong: "",
                    file: null,
                    preview: null
                };
            };
           $scope.onSelectImageCTSP = function (files) {
               let file = files[0];
               if (!file) return;

               $scope.ctspForm.file = file;

               let reader = new FileReader();
               reader.onload = function (e) {
                   $scope.$apply(function () {
                       $scope.ctspForm.preview = e.target.result;
                   });
               };
               reader.readAsDataURL(file);
           };
           $scope.saveCTSP = function () {
               let ct = $scope.ctspForm;
               $scope.ctspError = "";

               if (!ct.sizeId) return $scope.ctspError = "Chọn size";
               if (!ct.mauId) return $scope.ctspError = "Chọn màu";
               if (!ct.gia || !/^\d+$/.test(ct.gia)) return $scope.ctspError = "Giá không hợp lệ";
               if (!ct.soLuong || !/^\d+$/.test(ct.soLuong)) return $scope.ctspError = "Số lượng không hợp lệ";

               let formData = new FormData();
               formData.append("sizeId", ct.sizeId);
               formData.append("mauId", ct.mauId);
               formData.append("gia", ct.gia);
               formData.append("soLuong", ct.soLuong);
               if (ct.file) formData.append("image", ct.file);

               let url = $scope.isEditCTSP
                   ? "http://localhost:8084/san-pham/ctsp/update/" + ct.id
                   : "http://localhost:8084/san-pham/ctsp/add";

               if (!$scope.isEditCTSP) {
                   formData.append("sanPhamId", ct.sanPhamId);
               }

               $http({
                   method: $scope.isEditCTSP ? "PUT" : "POST",
                   url: url,
                   data: formData,
                   transformRequest: angular.identity,
                   headers: { "Content-Type": undefined }
               }).then(function (res) {
                   $scope.ctspSuccess = res.data.message || "Thành công";
                   $scope.openCTSP($scope.currentSanPhamId);
                   $scope.showAddCTSP = false;

                   setTimeout(() => $scope.ctspSuccess = "", 3000);
               }).catch(function (err) {
                   $scope.ctspError = err.data?.message || "Thao tác thất bại";
               });
           };


           $scope.addCTSP = function () {
               $scope.ctspError = "";
               $scope.ctspSuccess = "";
               let ct = $scope.newCTSP;
               ct.sanPhamId = $scope.currentSanPhamId;

               if (!ct.sanPhamId) {
                   $scope.ctspError = "Không xác định được sản phẩm";
                   return;
               }
               if (!ct.sizeId) {
                   $scope.ctspError = "Vui lòng chọn size";
                   return;
               }
               if (!ct.mauId) {
                   $scope.ctspError = "Vui lòng chọn màu sắc";
                   return;
               }
               if (!ct.gia || !/^\d+$/.test(ct.gia)) {
                   $scope.ctspError = "Giá không hợp lệ";
                   return;
               }
               if (!ct.soLuong || !/^\d+$/.test(ct.soLuong)) {
                   $scope.ctspError = "Số lượng không hợp lệ";
                   return;
               }
               if (!ct.file) {
                   $scope.ctspError = "Vui lòng chọn ảnh";
                   return;
               }
               let formData = new FormData();
               formData.append("sanPhamId", ct.sanPhamId);
               formData.append("sizeId", ct.sizeId);
               formData.append("mauId", ct.mauId);
               formData.append("gia", ct.gia);
               formData.append("soLuong", ct.soLuong);
               formData.append("image", ct.file);
               $http.post("http://localhost:8084/san-pham/ctsp/add", formData, {
                   transformRequest: angular.identity,
                   headers: { "Content-Type": undefined }
               }).then(function (res) {
                   $scope.openCTSP($scope.currentSanPhamId);
                   $scope.newCTSP = {
                       sanPhamId: $scope.currentSanPhamId,
                       sizeId: "",
                       mauId: "",
                       gia: "",
                       soLuong: "",
                       file: null,
                       preview: null
                   };
                   $scope.showAddCTSP = false;
                   $scope.ctspSuccess = res.data.message || "Thêm CTSP thành công";
                    $scope.ctspError = "";
                       $timeout(function () {
                           $scope.ctspSuccess = "";
                       }, 3000);
               }).catch(function (err) {
                   $scope.ctspError =
                       err.data?.message || "Thêm chi tiết sản phẩm thất bại";
               });
           };
          $scope.openEditCTSP = function (ctId) {
              $scope.showAddCTSP = true;
              $scope.isEditCTSP = true;
              $scope.ctspError = "";
              $scope.ctspSuccess = "";

              $http.get("http://localhost:8084/san-pham/detailsp/" + ctId)
                  .then(function (res) {
                      let ct = res.data;

                      $scope.ctspForm = {
                          id: ct.id,
                          sanPhamId: ct.sanPhamId,
                          sizeId: ct.sizeId,
                          tenSize: ct.tenSize,
                          mauId: ct.mauId,
                          tenMau: ct.tenM,
                          gia: ct.gia,
                          soLuong: ct.soLuong,
                          file: null,
                          preview:"http://localhost:8084/uploads/"+ct.img
                      };
                  }).catch(function () {
                      $scope.ctspError = "Không lấy được chi tiết sản phẩm";
                  });
          };


           $scope.cancelCTSP = function () {
               $scope.showAddCTSP = false;
               $scope.isEditCTSP = false;
               $scope.ctspError = "";
           };


           $scope.updateCTSP = function () {
               $scope.ctspError = "";
               $scope.ctspSuccess = "";

               let ct = $scope.editCTSP;

               if (!ct || !ct.id) {
                   $scope.ctspError = "Không xác định được CTSP";
                   return;
               }
               if (!ct.sizeId) {
                   $scope.ctspError = "Vui lòng chọn size";
                   return;
               }
               if (!ct.mauId) {
                   $scope.ctspError = "Vui lòng chọn màu sắc";
                   return;
               }
               if (!ct.gia || !/^\d+$/.test(ct.gia)) {
                   $scope.ctspError = "Giá không hợp lệ";
                   return;
               }
               if (!ct.soLuong || !/^\d+$/.test(ct.soLuong)) {
                   $scope.ctspError = "Số lượng không hợp lệ";
                   return;
               }

               let formData = new FormData();
               formData.append("sizeId", ct.sizeId);
               formData.append("mauId", ct.mauId);
               formData.append("gia", ct.gia);
               formData.append("soLuong", ct.soLuong);

               if (ct.file) {
                   formData.append("image", ct.file);
               }

               $http.put("http://localhost:8084/san-pham/ctsp/update/" + ct.id, formData, {
                   transformRequest: angular.identity,
                   headers: { "Content-Type": undefined }
               }).then(function (res) {

                   $scope.ctspSuccess = res.data.message || "Cập nhật CTSP thành công";

                   $scope.openCTSP(ct.sanPhamId);
                   setTimeout(() => {
                       $scope.$apply(() => {
                           $scope.ctspSuccess = "";
                       });
                   }, 3000);
               }).catch(function (err) {
                   $scope.ctspError =
                       err.data?.message || "Cập nhật chi tiết sản phẩm thất bại";
               });
           };
        $scope.loadSanPham();


    });
