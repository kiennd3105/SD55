            app.controller("taiQuayCtrl", function ($scope, $http) {

                $scope.dsHoaDon = [];
                $scope.dsHDCT = [];
                $scope.hoaDonDangXem = null;
                $scope.tongTien = 0;
                $scope.dsSanPham = [];
                $scope.hienFormThemKH = false;
                $scope.khMoi = {};
                $scope.dsVoucher = [];


                 $scope.loadSanPham = function () {
                        $http.get("http://localhost:8084/ban-hang/dang-ban")
                            .then(function (res) {
                                $scope.dsSanPham = res.data;
                              console.log("Reload sản phẩm:", res.data);

                            }, function (err) {
                                console.error("Lỗi load sản phẩm", err);
                            });
                    };
                    $scope.timSanPham = function (sp) {
                        if (!$scope.keyword) return true;

                        let key = $scope.keyword.toLowerCase();

                        return (sp.tenSP && sp.tenSP.toLowerCase().includes(key))
                            || (sp.ma && sp.ma.toLowerCase().includes(key));
                    };
                $scope.loadHoaDon = function () {
                    $http.get("http://localhost:8084/ban-hang/hoa-don/tai-quay")
                        .then(function (res) {
                            $scope.dsHoaDon = res.data;

                            if ($scope.dsHoaDon.length > 0) {
                                $scope.hoaDonDangXem = $scope.dsHoaDon[0];
                                $scope.loadCTHD($scope.hoaDonDangXem.id);
                            }
                        });
                };

                $scope.loadCTHD = function (idHoaDon) {
                    $http.get("http://localhost:8084/ban-hang/hoa-don/detail/" + idHoaDon)
                        .then(function (res) {
                            $scope.dsHDCT = res.data || [];
                        console.log("Reload cthd:", res.data);
                             $scope.dsHDCT.forEach(ct => {ct.soLuong = parseInt(ct.soLuong);});
                            $scope.tinhTienTam();

                            $scope.loadVoucherApDung();
                        });
                };
                $scope.capNhatSoLuong = function (ct) {

                    if (ct.soLuong <= 0) {
                        ct.soLuong = 1;
                        return;
                    }
                    $http.post("http://localhost:8084/ban-hang/hoa-don/cap-nhat-so-luong", null, {
                        params: {
                            idHDCT: ct.id,
                            soLuongMoi: ct.soLuong
                        }
                    }).then(function (res) {
                        ct.soLuong = res.data.soLuong;
                      $scope.loadCTHD($scope.hoaDonDangXem.id);
                      $scope.reloadHoaDon();
                    }, function (err) {
                        alert(err.data.message || "Không đủ tồn kho");
                        $scope.loadCTHD($scope.hoaDonDangXem.id);
                    });
                };
                $scope.xoaCTHD = function (ct) {
                    if (!confirm("Xóa sản phẩm khỏi hóa đơn?")) return;
                    $http.delete("http://localhost:8084/ban-hang/hoa-don/xoa-san-pham/" + ct.id)
                        .then(function () {
                            $scope.loadCTHD($scope.hoaDonDangXem.id);
                            $scope.reloadHoaDon();
                        }, function (err) {
                            alert(err.data?.message || "Lỗi xóa sản phẩm");
                        });
                };

                $scope.chonHoaDon = function (hd) {
                    $scope.hoaDonDangXem = hd;
                    $scope.loadCTHD(hd.id);
                };
                $scope.taoHoaDonMoi = function () {
                    $http.post(
                        "http://localhost:8084/ban-hang/hoa-don/add",
                        null,
                        { params: { maLoaiHoaDon: "TAI_QUAY" } }
                    ).then(function (res) {

                        let hoaDonMoi = res.data;

                        $scope.dsHoaDon.push(hoaDonMoi);

                        $scope.hoaDonDangXem = hoaDonMoi;

                        $scope.loadCTHD(hoaDonMoi.id);
                    });
                };



              $scope.reloadHoaDon = function () {
                  if (!$scope.hoaDonDangXem) return;

                  $http.get(
                      "http://localhost:8084/ban-hang/hoa-don/" + $scope.hoaDonDangXem.id
                  ).then(function (res) {
                      $scope.hoaDonDangXem = res.data;
                      $scope.loadVoucherApDung();
                      $scope.tinhTienTam();
                  });
              };

               $scope.themSanPham = function (sp) {

                   if (!sp.soLuongThem || sp.soLuongThem <= 0) {
                       alert("Số lượng không hợp lệ");
                       return;
                   }

                   if (sp.soLuongThem > sp.soLuong) {
                       alert("Số lượng vượt quá tồn kho");
                       return;
                   }

                   $http.post("http://localhost:8084/ban-hang/hoa-don/them-san-pham", null, {
                       params: {
                           idHoaDon: $scope.hoaDonDangXem.id,
                           idSPCT: sp.id,
                           soLuong: sp.soLuongThem
                       }
                   }).then(function () {
                       $scope.loadCTHD($scope.hoaDonDangXem.id);
                       sp.soLuongThem = 1;

                   }, function (err) {
                       alert(err.data?.message || "Lỗi thêm sản phẩm");
                   });
               };
               $scope.dongHoaDon = function (hd, $event) {
                   $event.stopPropagation();

                   if (!confirm("Bạn có chắc muốn xóa hóa đơn này?")) return;

                   $http.delete("http://localhost:8084/ban-hang/hoa-don/xoa/" + hd.id)
                       .then(function () {

                           let index = $scope.dsHoaDon.indexOf(hd);
                           if (index >= 0) {
                               $scope.dsHoaDon.splice(index, 1);
                           }

                           if ($scope.hoaDonDangXem && $scope.hoaDonDangXem.id === hd.id) {
                               $scope.hoaDonDangXem = null;
                               $scope.dsHDCT = [];
                               $scope.tongTien = 0;
                           }

                       }, function (err) {
                           alert("Lỗi xóa hóa đơn");
                           console.error(err);
                       });
               };
                $scope.moModalChonKhachHang = function () {
                    $('#modalKhachHang').modal('show');
                    $http.get("http://localhost:8084/khach-hang/hien-thi")
                        .then(res => {
                            $scope.dsKhachHang = res.data;
                        });
                };

                $scope.chonKhachHang = function (kh) {
                    let hd = $scope.hoaDonDangXem;
                    if (!hd) return;

                    $http.post("http://localhost:8084/ban-hang/hoa-don/them-khach-hang", null, {
                        params: {
                            idHoaDon: hd.id,
                            idKhachHang: kh.id
                        }
                    }).then(res => {
                        hd.khachHang = kh;
                        $('#modalKhachHang').modal('hide');
                    }, err => {
                        alert(err.data.message || "Không thể thêm khách hàng");
                    });
                };
                $scope.xoaKhachHang = function () {
                    let hd = $scope.hoaDonDangXem;
                    if (!hd) return;

                    if (!confirm("Bạn có chắc muốn xóa khách hàng khỏi hóa đơn?")) return;

                    $http.post("http://localhost:8084/ban-hang/hoa-don/xoa-khach-hang", null, {
                        params: {
                            idHoaDon: hd.id
                        }
                    }).then(res => {
                        hd.khachHang = null;
                    }, err => {
                        alert(err.data.message || "Không thể xóa khách hàng");
                    });
                };
                $scope.luuKhachHangMoi = function () {
                    if (!$scope.khMoi.ten || !$scope.khMoi.sdt) {
                        alert("Vui lòng nhập đủ thông tin");
                        return;
                    }
                    $http.post("http://localhost:8084/khach-hang/add", $scope.khMoi)
                        .then(function (res) {
                            let kh = res.data;
                            $scope.dsKhachHang.unshift(kh);
                            $scope.chonKhachHang(kh);
                            $scope.khMoi = {};
                            $scope.hienFormThemKH = false;

                        }, function () {
                            alert("Lỗi thêm khách hàng");
                        });
                };
                $scope.loadVoucherApDung = function () {
                    if (!$scope.hoaDonDangXem) return;

                    $http.get(
                        "http://localhost:8084/ban-hang/hoa-don/voucher-ap-dung/"
                        + $scope.hoaDonDangXem.id
                    ).then(function (res) {
                        $scope.dsVoucher = res.data;
                    }, function () {
                        $scope.dsVoucher = [];
                    });
                };
               $scope.chonVoucher = function (vc) {

                   let hd = $scope.hoaDonDangXem;
                   if (!hd) return;
                    console.log("HD voucher:", hd.voucher);
                    console.log("Click voucher:", vc);

                   // 🔁 CLICK LẠI → HỦY VOUCHER
                   if (hd.voucher && hd.voucher.idVoucher === vc.idVoucher) {

                       $http.post(
                           "http://localhost:8084/ban-hang/hoa-don/huy-voucher",
                           null,
                           { params: { idHoaDon: hd.id } }
                       ).then(res => {

                           // backend trả về hóa đơn KHÔNG voucher
                           $scope.hoaDonDangXem = res.data;

                           // đảm bảo clear voucher
                           $scope.hoaDonDangXem.voucher = null;

                           $scope.loadVoucherApDung();
                           $scope.tinhTienTam();
                       });

                       return;
                   }

                   // ➕ ÁP DỤNG VOUCHER
                   $http.post(
                       "http://localhost:8084/ban-hang/hoa-don/ap-dung-voucher",
                       null,
                       {
                           params: {
                               idHoaDon: hd.id,
                               idVoucher: vc.idVoucher
                           }
                       }
                   ).then(res => {

                       $scope.hoaDonDangXem = res.data;

                       $scope.loadVoucherApDung();
                       $scope.tinhTienTam();

                   }, err => {
                       alert(err.data?.message || "Không thể áp dụng voucher");
                   });
               };




               $scope.xacNhanThanhToan = function () {

                   if ($scope.phuongThucTT === 'TIEN_MAT' && $scope.tienThoi < 0) {
                       alert("Tiền khách đưa chưa đủ");
                       return;
                   }

                   $http.post(
                       "http://localhost:8084/ban-hang/hoa-don/thanh-toan",
                       null,
                       {
                           params: {
                               idHoaDon: $scope.hoaDonDangXem.id,
                               phuongThuc: $scope.phuongThucTT
                           }
                       }
                   ).then(function (res) {

                       alert("Thanh toán thành công");
                       window.location.reload();
                       $('#modalThanhToan').modal('hide');

                       $scope.hoaDonDangXem = null;
                       $scope.loadHoaDonTaiQuay();

                   }, function (err) {
                       alert(err.data?.message || "Thanh toán thất bại");
                   });
               };
                $scope.moModalThanhToan = function () {
                    if (!$scope.hoaDonDangXem) return;

                    $scope.phuongThucTT = "TIEN_MAT";
                     $scope.thanhToan = {
                            tienKhachDua: null,
                            tienThoi: 0
                        };
                         $scope.tinhTienTam();
                    $('#modalThanhToan').modal('show');
                };
                $scope.tinhTienThoi = function () {
                    let canThanhToan = Number($scope.thanhTien || 0);
                    let tienKhachDua = Number($scope.thanhToan.tienKhachDua || 0);

                    $scope.thanhToan.tienThoi = tienKhachDua - canThanhToan;
                };

                $scope.tinhTienTam = function () {
                    if (!$scope.hoaDonDangXem) return;

                    $http.get(
                        "http://localhost:8084/ban-hang/hoa-don/tinh-tien/"
                        + $scope.hoaDonDangXem.id
                    ).then(function (res) {

                        $scope.tongTien = res.data.tongTien;
                        $scope.giamGia = res.data.giamGia;
                        $scope.thanhTien = res.data.thanhTien;

                        $scope.hoaDonDangXem.tongTien = res.data.tongTien;
                        $scope.hoaDonDangXem.giamGia = res.data.giamGia;
                        $scope.hoaDonDangXem.thanhTien = res.data.thanhTien;

                    });
                };


                $scope.loadSanPham();
                $scope.loadHoaDon();
            });
