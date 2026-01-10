app.controller("voucherCtrl", function ($scope, $http, $timeout) {
    // Khởi tạo tất cả biến
    $scope.dsVoucher = [];
    $scope.voucherDetail = {};
    $scope.currentPage = 0;
    $scope.pageSize = 10;
    $scope.totalPages = 0;
    $scope.totalElements = 0;
    $scope.numberOfElements = 0;
    $scope.searchKeyword = "";
    
    // Đảm bảo các modal flags luôn là false khi khởi tạo
    $scope.showAddModal = false;
    $scope.showEditModal = false;
    $scope.showDeleteModal = false;
    $scope.voucherToDelete = null;
    
    // Xử lý phím ESC để đóng modal
    $scope.$on('$destroy', function() {
        angular.element(document).off('keydown');
    });
    
    angular.element(document).on('keydown', function(event) {
        if (event.keyCode === 27) { // ESC key
            if ($scope.showAddModal) {
                $scope.$apply(function() {
                    $scope.closeAddModal();
                });
            } else if ($scope.showEditModal) {
                $scope.$apply(function() {
                    $scope.closeEditModal();
                });
            } else if ($scope.showDeleteModal) {
                $scope.$apply(function() {
                    $scope.closeDeleteModal();
                });
            }
        }
    });

    // Load danh sách voucher với phân trang
    $scope.loadDanhSach = function (page = 0) {
        var url = "http://localhost:8084/admin/voucher/find-all?page=" + page + "&size=" + $scope.pageSize;
        
        if ($scope.searchKeyword && $scope.searchKeyword.trim() !== "") {
            url += "&search=" + encodeURIComponent($scope.searchKeyword.trim());
        }

        $http.get(url)
            .then(function (response) {
                if (response.data) {
                    $scope.dsVoucher = response.data.content || [];
                    $scope.currentPage = response.data.number || 0;
                    $scope.totalPages = response.data.totalPages || 0;
                    $scope.totalElements = response.data.totalElements || 0;
                    $scope.numberOfElements = response.data.numberOfElements || 0;
                    console.log("Voucher data:", response.data);
                } else {
                    console.error("Response data is null");
                    $scope.dsVoucher = [];
                }
            })
            .catch(function (error) {
                console.error("Lỗi load voucher", error);
                // Giữ nguyên giá trị phân trang hiện tại khi có lỗi
                if (!$scope.dsVoucher || $scope.dsVoucher.length === 0) {
                    $scope.dsVoucher = [];
                    $scope.totalPages = 0;
                    $scope.totalElements = 0;
                    $scope.numberOfElements = 0;
                }
                alert("Lỗi khi tải danh sách voucher");
            });
    };

    // Khởi tạo load danh sách
    $scope.loadDanhSach(0);

    // Tìm kiếm
    $scope.search = function () {
        $scope.currentPage = 0;
        $scope.loadDanhSach(0);
    };

    // Phân trang
    $scope.goToPage = function (page, event) {
        if (event) {
            event.preventDefault();
        }
        if (page >= 0 && page < $scope.totalPages) {
            $scope.loadDanhSach(page);
        }
    };

    $scope.previousPage = function (event) {
        if (event) {
            event.preventDefault();
        }
        if ($scope.currentPage > 0) {
            $scope.goToPage($scope.currentPage - 1);
        }
    };

    $scope.nextPage = function (event) {
        if (event) {
            event.preventDefault();
        }
        if ($scope.currentPage < $scope.totalPages - 1) {
            $scope.goToPage($scope.currentPage + 1);
        }
    };

    // Format số tiền với dấu chấm phân cách
    $scope.formatNumber = function (value) {
        if (!value) return "";
        // Bỏ hết ký tự không phải số
        value = String(value).replace(/\D/g, "");
        if (!value) return "";
        // Thêm dấu . mỗi 3 số
        return value.replace(/\B(?=(\d{3})+(?!\d))/g, ".");
    };

    // Bỏ format số tiền (bỏ dấu chấm)
    $scope.unformatNumber = function (value) {
        if (!value) return "";
        return String(value).replace(/\./g, "");
    };

    // Mở modal thêm mới
    $scope.openAddModal = function () {
        $scope.voucherDetail = {
            ma: "",
            ten: "",
            loai: 1, // 1: Theo %, 2: Theo giá tiền
            dieuKienApDung: "",
            giamMax: "",
            giaTriGiam: "",
            soLuong: "",
            ngayBatDau: "",
            ngayKetThuc: "",
            trangThai: 1,
            moTa: ""
        };
        $scope.showAddModal = true;
    };

    $scope.closeAddModal = function () {
        $scope.showAddModal = false;
        $scope.voucherDetail = {};
    };

    // Thêm voucher mới
    $scope.addVoucher = function () {
        // if ($scope.addForm.$invalid) {
        //     alert("Vui lòng nhập đầy đủ và đúng thông tin!");
        //     return;
        // }

        // Format ngày tháng - chuyển từ datetime-local sang ISO string
        var voucherData = angular.copy($scope.voucherDetail);
        
        // Bỏ format số tiền trước khi gửi (bỏ dấu chấm)
        if (voucherData.giaTriGiam) {
            voucherData.giaTriGiam = $scope.unformatNumber(voucherData.giaTriGiam);
        }
        if (voucherData.giamMax) {
            voucherData.giamMax = $scope.unformatNumber(voucherData.giamMax);
        }
        if (voucherData.dieuKienApDung) {
            voucherData.dieuKienApDung = $scope.unformatNumber(voucherData.dieuKienApDung);
        }
        
        if (voucherData.ngayBatDau) {
            var date = new Date(voucherData.ngayBatDau);
            voucherData.ngayBatDau = date.toISOString();
        }
        if (voucherData.ngayKetThuc) {
            var date = new Date(voucherData.ngayKetThuc);
            voucherData.ngayKetThuc = date.toISOString();
        }

        $http.post("http://localhost:8084/admin/voucher/create-or-update", voucherData)
            .then(function (response) {
                alert("Thêm voucher thành công!");
                $scope.closeAddModal();
                $scope.loadDanhSach($scope.currentPage);
            })
            .catch(function (error) {
                console.error("Lỗi thêm voucher", error);
                var errorMsg = "Thêm voucher thất bại";
                if (error.data && error.data.message) {
                    errorMsg += ": " + error.data.message;
                } else if (error.data && typeof error.data === 'string') {
                    errorMsg += ": " + error.data;
                }
                alert(errorMsg);
            });
    };

    // Mở modal chỉnh sửa
    $scope.openEditModal = function (id) {
        $http.get("http://localhost:8084/admin/voucher/find-by-id?id=" + id)
            .then(function (response) {
                var data = response.data;

                // Tạo object mới thay vì gán trực tiếp để tránh lỗi ngModel:datefmt
                $scope.voucherDetail = {
                    idVoucher: data.idVoucher,
                    ma: data.ma,
                    ten: data.ten,
                    moTa: data.moTa || ""
                };

                // Convert loai sang string
                if (data.loai !== undefined && data.loai !== null) {
                    $scope.voucherDetail.loai = String(data.loai);
                }

                // Convert trangThai sang string
                if (data.trangThai !== undefined && data.trangThai !== null) {
                    $scope.voucherDetail.trangThai = String(data.trangThai);
                }

                // Convert soLuong
                if (data.soLuong !== undefined && data.soLuong !== null && data.soLuong !== "") {
                    $scope.voucherDetail.soLuong = Number(data.soLuong);
                } else {
                    $scope.voucherDetail.soLuong = "";
                }

                // Format số tiền với dấu chấm phân cách
                if (data.giaTriGiam) {
                    $scope.voucherDetail.giaTriGiam = $scope.formatNumber(data.giaTriGiam);
                } else {
                    $scope.voucherDetail.giaTriGiam = "";
                }

                if (data.giamMax) {
                    $scope.voucherDetail.giamMax = $scope.formatNumber(data.giamMax);
                } else {
                    $scope.voucherDetail.giamMax = "";
                }

                if (data.dieuKienApDung) {
                    $scope.voucherDetail.dieuKienApDung = $scope.formatNumber(data.dieuKienApDung);
                } else {
                    $scope.voucherDetail.dieuKienApDung = "";
                }

                if (data.ngayBatDau) {
                    var ngayBatDauStr = String(data.ngayBatDau);
                    if (ngayBatDauStr.includes('T')) {
                        var parts = ngayBatDauStr.split('T');
                        if (parts.length === 2) {
                            var datePart = parts[0];
                            var timePart = parts[1].split('.')[0].split('Z')[0].split('+')[0];
                            if (timePart.length > 5) {
                                timePart = timePart.substring(0, 5);
                            }
                            $scope.voucherDetail.ngayBatDau = datePart + 'T' + timePart;
                        } else {
                            $scope.voucherDetail.ngayBatDau = "";
                        }
                    } else {
                        $scope.voucherDetail.ngayBatDau = "";
                    }
                } else {
                    $scope.voucherDetail.ngayBatDau = "";
                }

                if (data.ngayKetThuc) {
                    var ngayKetThucStr = String(data.ngayKetThuc);
                    if (ngayKetThucStr.includes('T')) {
                        var parts = ngayKetThucStr.split('T');
                        if (parts.length === 2) {
                            var datePart = parts[0];
                            var timePart = parts[1].split('.')[0].split('Z')[0].split('+')[0];
                            if (timePart.length > 5) {
                                timePart = timePart.substring(0, 5);
                            }
                            $scope.voucherDetail.ngayKetThuc = datePart + 'T' + timePart;
                        } else {
                            $scope.voucherDetail.ngayKetThuc = "";
                        }
                    } else {
                        $scope.voucherDetail.ngayKetThuc = "";
                    }
                } else {
                    $scope.voucherDetail.ngayKetThuc = "";
                }

                console.log("Final voucherDetail:", $scope.voucherDetail);
                $scope.showEditModal = true;
                $timeout(function() {
                    var ngayBatDauInput = document.querySelector('input[name="ngayBatDau"]');
                    var ngayKetThucInput = document.querySelector('input[name="ngayKetThuc"]');

                    if (ngayBatDauInput && $scope.voucherDetail.ngayBatDau) {
                        ngayBatDauInput.value = $scope.voucherDetail.ngayBatDau;
                        // Trigger input event để Angular cập nhật
                        angular.element(ngayBatDauInput).triggerHandler('input');
                    }

                    if (ngayKetThucInput && $scope.voucherDetail.ngayKetThuc) {
                        ngayKetThucInput.value = $scope.voucherDetail.ngayKetThuc;
                        // Trigger input event để Angular cập nhật
                        angular.element(ngayKetThucInput).triggerHandler('input');
                    }
                }, 200);
            })
            .catch(function (error) {
                console.error("Lỗi lấy chi tiết voucher", error);
                alert("Không thể lấy thông tin voucher");
            });
    };

    $scope.closeEditModal = function () {
        $scope.showEditModal = false;
        $scope.voucherDetail = {};
    };

    // Cập nhật voucher
    $scope.updateVoucher = function () {
        // if ($scope.editForm.$invalid) {
        //     alert("Vui lòng nhập đầy đủ và đúng thông tin!");
        //     return;
        // }

        // Format ngày tháng - chuyển từ datetime-local sang ISO string
        var voucherData = angular.copy($scope.voucherDetail);
        
        // Bỏ format số tiền trước khi gửi (bỏ dấu chấm)
        if (voucherData.giaTriGiam) {
            voucherData.giaTriGiam = $scope.unformatNumber(voucherData.giaTriGiam);
        }
        if (voucherData.giamMax) {
            voucherData.giamMax = $scope.unformatNumber(voucherData.giamMax);
        }
        if (voucherData.dieuKienApDung) {
            voucherData.dieuKienApDung = $scope.unformatNumber(voucherData.dieuKienApDung);
        }
        
        if (voucherData.ngayBatDau) {
            var date = new Date(voucherData.ngayBatDau);
            voucherData.ngayBatDau = date.toISOString();
        }
        if (voucherData.ngayKetThuc) {
            var date = new Date(voucherData.ngayKetThuc);
            voucherData.ngayKetThuc = date.toISOString();
        }

        $http.post("http://localhost:8084/admin/voucher/create-or-update", voucherData)
            .then(function (response) {
                alert("Cập nhật voucher thành công!");
                $scope.closeEditModal();
                $scope.loadDanhSach($scope.currentPage);
            })
            .catch(function (error) {
                console.error("Lỗi cập nhật voucher", error);
                var errorMsg = "Cập nhật voucher thất bại";
                if (error.data && error.data.message) {
                    errorMsg += ": " + error.data.message;
                } else if (error.data && typeof error.data === 'string') {
                    errorMsg += ": " + error.data;
                }
                alert(errorMsg);
            });
    };

    // Mở modal xóa
    $scope.openDeleteModal = function (voucher) {
        console.log("openDeleteModal called with:", voucher);
        $scope.voucherToDelete = voucher;
        console.log($scope.voucherToDelete)
        $scope.showDeleteModal = true;
    };

    $scope.closeDeleteModal = function () {
        $scope.showDeleteModal = false;
        $scope.voucherToDelete = null;
    };

    // Xóa voucher
    $scope.deleteVoucher = function () {
        if (!$scope.voucherToDelete) {
            return;
        }

        $http.delete("http://localhost:8084/admin/voucher/delete?id=" + $scope.voucherToDelete.idVoucher)
            .then(function (response) {
                if (response.data && response.data.success) {
                    alert(response.data.message || "Xóa voucher thành công!");
                } else {
                    alert("Xóa voucher thành công!");
                }
                $scope.closeDeleteModal();
                $scope.loadDanhSach($scope.currentPage);
            })
            .catch(function (error) {
                console.error("Lỗi xóa voucher", error);
                var errorMsg = "Xóa voucher thất bại";
                if (error.data && error.data.message) {
                    errorMsg += ": " + error.data.message;
                } else if (error.data && typeof error.data === 'string') {
                    errorMsg += ": " + error.data;
                }
                alert(errorMsg);
            });
    };

    // Format ngày tháng hiển thị
    $scope.formatDate = function (dateString) {
        if (!dateString) return "";
        var date = new Date(dateString);
        return date.toLocaleString("vi-VN");
    };

    // Format số tiền hiển thị (có dấu chấm phân cách, không có đ)
    $scope.formatCurrency = function (value) {
        if (!value) return "0";
        return $scope.formatNumber(value);
    };

    // Format ngày tạo hiển thị
    $scope.formatNgayTao = function (dateString) {
        if (!dateString) return "";
        var date = new Date(dateString);
        var hours = String(date.getHours()).padStart(2, '0');
        var minutes = String(date.getMinutes()).padStart(2, '0');
        var day = String(date.getDate()).padStart(2, '0');
        var month = String(date.getMonth() + 1).padStart(2, '0');
        var year = date.getFullYear();
        return hours + ":" + minutes + " " + day + "/" + month + "/" + year;
    };

    // Tạo mảng số trang để hiển thị
    $scope.getPageNumbers = function () {
        var pages = [];
        var total = $scope.totalPages || 0;
        for (var i = 0; i < total; i++) {
            pages.push(i);
        }
        return pages;
    };

    // Thay đổi kích thước trang
    $scope.changePageSize = function () {
        $scope.currentPage = 0;
        $scope.loadDanhSach(0);
    };

    // Xử lý format số khi nhập vào input
    $scope.onNumberInput = function (field, event) {
        if (!$scope.voucherDetail[field]) return;
        
        var input = event ? event.target : document.getElementById(field);
        if (!input) return;
        
        var cursor = input.selectionStart;
        var value = $scope.unformatNumber($scope.voucherDetail[field]);
        
        if (value) {
            var formatted = $scope.formatNumber(value);
            $scope.voucherDetail[field] = formatted;
            
            // Giữ vị trí cursor sau khi format
            $timeout(function() {
                var newCursor = Math.min(cursor + (formatted.length - String(value).length), formatted.length);
                input.setSelectionRange(newCursor, newCursor);
            }, 0);
        } else {
            $scope.voucherDetail[field] = "";
        }
    };
});

