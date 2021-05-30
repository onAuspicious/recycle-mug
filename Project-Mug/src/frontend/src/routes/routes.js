import Vue from "vue";
import Router from "vue-router";
import axios from "axios";

Vue.use(Router);

import MainView from "../views/MainView";
import JoinView from "../views/JoinView";
import LoginView from "../views/LoginView";
import ProfileView from "../views/ProfileView";
import AddressPopup from "../components/AddressPopup";

export default new Router({
  mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "main",
      component: MainView,
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
      props: true,
    },
    {
      path: "/join",
      name: "join",
      component: JoinView,
      props: true,
    },
    {
      path: "/profile",
      name: "profile",
      component: ProfileView,
    },
    {
      path: "/address",
      name: "address",
      component: AddressPopup,
    },
  ],
});
