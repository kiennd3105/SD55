var app = angular.module("myApp", ['ngRoute'])

app.config(function ($routeProvider) {
    $routeProvider
       .when('/sanpham', {
           templateUrl: '/ban_tai_quay/view/sanpham.html',
              controller: 'sanPhamCtrl'
       })
        .when("/hoadon", {
            templateUrl: "/ban_tai_quay/view/hoadon.html",
            controller: "hoaDonCtrl"
        })
        .when('/theloai', {
                  templateUrl: '/ban_tai_quay/view/theloai.html',
                  controller: 'theLoaiCtrl'
              })
       .when('/trangchu', {
           templateUrl: '/ban_tai_quay/view/trangchu.html'
       })
        .when('/nhanvien', {
            templateUrl: '/ban_tai_quay/view/nhanvien.html',
            controller: 'nhanVienCtrl'
        })
        .when('/khachhang', {
            templateUrl: '/ban_tai_quay/view/khachhang.html',
            controller: 'khachHangCtrl'
        })
        .when('/thuonghieu', {
            templateUrl: '/ban_tai_quay/view/thuonghieu.html',
            controller: 'thuongHieuCtrl'
        })
        .when('/mausac', {
            templateUrl: '/ban_tai_quay/view/mausac.html',
            controller: 'mauSacCtrl'
        })
        .when('/chatlieu', {
            templateUrl: '/ban_tai_quay/view/chatlieu.html',
            controller: 'chatLieuCtrl'
        })
        .when('/size', {
            templateUrl: '/ban_tai_quay/view/size.html',
            controller: 'sizeCtrl'
        })
        .otherwise({
            redirectTo: '/theloai',
        })
})

app.controller('myCtrl', function ($scope, $http) {
})
document.addEventListener('DOMContentLoaded', function () {
    const allSideMenu = document.querySelectorAll('#sidebar .side-menu.top li a')

    allSideMenu.forEach(item => {
        const li = item.parentElement

        item.addEventListener('click', function () {
            allSideMenu.forEach(i => {
                i.parentElement.classList.remove('active')
            })
            li.classList.add('active')
        })
    })

    const menuBar = document.querySelector('#bx_menu')
    const sidebar = document.getElementById('sidebar')

    menuBar.addEventListener('click', function () {
        sidebar.classList.toggle('hide');
    })
})