<template>
  <div class="return-table-card-container">
    <div class="return-table-card">
      <div class="orderlist-wrapper">
        <div class="orderlist-header">
          <div class="header-nav">
            <span>회수 대기 중인 컵</span>
          </div>
        </div>
        <div class="orderlist-body">
          <table class="orderlist-table">
            <colgroup>
              <col class="selection-col" />
            </colgroup>

            <thead class="table-header">
              <tr>
                <th class="selection-column">
                  <input type="checkbox" name="" id="" @input="checkAll" v-model="allSelected" />
                </th>
                <th class="id">#</th>
                <th class="customer">Customer</th>
                <th class="cup">Cup</th>
                <th class="date">갯수</th>
                <th class="action">Action</th>
              </tr>
            </thead>

            <tbody class="table-body">
              <tr v-for="index in parseInt(perPage)" :key="index">
                <td
                  class="selection-column"
                  v-if="entryList[(currentPage - 1) * perPage + index - 1]"
                >
                  <input
                    type="checkbox"
                    name=""
                    id=""
                    :value="(currentPage - 1) * perPage + index - 1"
                    v-model="orderIds"
                    @click="checkOne"
                  />
                </td>
                <td class="id" v-if="entryList[(currentPage - 1) * perPage + index - 1]">
                  <span>{{ entryList.length - ((currentPage - 1) * perPage + index - 1) }}</span>
                </td>
                <td class="customer" v-if="entryList[(currentPage - 1) * perPage + index - 1]">
                  <span>{{ entryList[(currentPage - 1) * perPage + index - 1].businessName }}</span>
                </td>
                <td class="cup" v-if="entryList[(currentPage - 1) * perPage + index - 1]">
                  <span>{{ entryList[(currentPage - 1) * perPage + index - 1].cupName }}</span>
                </td>
                <td class="date" v-if="entryList[(currentPage - 1) * perPage + index - 1]">
                  <span
                    >{{ entryList[(currentPage - 1) * perPage + index - 1].returnQuantity }}개</span
                  >
                </td>
                <td class="action" v-if="entryList[(currentPage - 1) * perPage + index - 1]">
                  <span
                    class="icon-wrapper submit"
                    @click="submitOrder((currentPage - 1) * perPage + index - 1)"
                  >
                    <font-awesome-icon :icon="['fas', 'check']"></font-awesome-icon>
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="orderlist-footer">
          <div class="footer-nav">
            <div class="footer-button-wrapper">
              <span class="footer-button submit" @click="submitSelectedOrders()">
                <font-awesome-icon :icon="['fas', 'check']"></font-awesome-icon>
              </span>
            </div>

            <ul role="menubar" aria-label="Pagination">
              <li role="presentation">
                <button
                  role="menuitem"
                  type="button"
                  tabindex="-1"
                  class="page-item prev-item"
                  @click="prevPage"
                  v-if="currentPage > 5"
                >
                  <font-awesome-icon :icon="['fas', 'angle-left']"></font-awesome-icon>
                </button>
              </li>
              <li role="presentation">
                <button
                  role="menuitem"
                  type="button"
                  tabindex="-1"
                  class="page-item "
                  :class="{
                    active: currentPage == (Math.ceil(currentPage / 5) - 1) * 5 + 1,
                  }"
                  @click="selectPage"
                  v-if="(Math.ceil(currentPage / 5) - 1) * 5 + 1 <= maxPage"
                >
                  {{ (Math.ceil(currentPage / 5) - 1) * 5 + 1 }}
                </button>
              </li>
              <li role="presentation">
                <button
                  role="menuitem"
                  type="button"
                  tabindex="-1"
                  class="page-item"
                  :class="{
                    active: currentPage == (Math.ceil(currentPage / 5) - 1) * 5 + 2,
                  }"
                  @click="selectPage"
                  v-if="(Math.ceil(currentPage / 5) - 1) * 5 + 2 <= maxPage"
                >
                  {{ (Math.ceil(currentPage / 5) - 1) * 5 + 2 }}
                </button>
              </li>
              <li role="presentation">
                <button
                  role="menuitem"
                  type="button"
                  tabindex="-1"
                  class="page-item"
                  :class="{
                    active: currentPage == (Math.ceil(currentPage / 5) - 1) * 5 + 3,
                  }"
                  @click="selectPage"
                  v-if="(Math.ceil(currentPage / 5) - 1) * 5 + 3 <= maxPage"
                >
                  {{ (Math.ceil(currentPage / 5) - 1) * 5 + 3 }}
                </button>
              </li>
              <li role="presentation">
                <button
                  role="menuitem"
                  type="button"
                  tabindex="-1"
                  class="page-item"
                  :class="{
                    active: currentPage == (Math.ceil(currentPage / 5) - 1) * 5 + 4,
                    'last-item': (Math.ceil(currentPage / 5) - 1) * 5 + 4 == maxPage,
                  }"
                  :style="[
                    currentPage == (Math.ceil(currentPage / 5) - 1) * 5 + 4
                      ? { backgroundColor: userStyle }
                      : {},
                  ]"
                  @click="selectPage"
                  v-if="(Math.ceil(currentPage / 5) - 1) * 5 + 4 <= maxPage"
                >
                  {{ (Math.ceil(currentPage / 5) - 1) * 5 + 4 }}
                </button>
              </li>
              <li role="presentation">
                <button
                  role="menuitem"
                  type="button"
                  tabindex="-1"
                  class="page-item"
                  :class="{
                    active: currentPage == (Math.ceil(currentPage / 5) - 1) * 5 + 5,
                    'last-item': (Math.ceil(currentPage / 5) - 1) * 5 + 5 == maxPage,
                  }"
                  @click="selectPage"
                  v-if="(Math.ceil(currentPage / 5) - 1) * 5 + 5 <= maxPage"
                >
                  {{ (Math.ceil(currentPage / 5) - 1) * 5 + 5 }}
                </button>
              </li>
              <li role="presentation">
                <button
                  role="menuitem"
                  type="button"
                  tabindex="-1"
                  class="page-item next-item"
                  @click="nextPage"
                  v-if="currentPage <= Math.floor((maxPage - 1) / 5) * 5"
                >
                  <font-awesome-icon :icon="['fas', 'angle-right']"></font-awesome-icon>
                </button>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faAngleLeft, faAngleRight, faCheck, faTimes } from "@fortawesome/free-solid-svg-icons";
import { library as faLibrary } from "@fortawesome/fontawesome-svg-core";

import axios from "axios";

faLibrary.add(faAngleLeft, faAngleRight, faCheck, faTimes);

export default {
  data() {
    return {
      total: 50,
      perPage: 10,
      maxPage: 20,
      currentPage: 1,
      entryList: [],
      orderIds: [],
      allSelected: false,
    };
  },
  components: { FontAwesomeIcon },
  methods: {
    nextPage() {
      this.currentPage = Math.ceil(this.currentPage / 5) * 5 + 1;
      this.allSelected = false;
      this.orderIds = [];
    },
    prevPage() {
      if (this.currentPage > 5) this.currentPage = Math.ceil(this.currentPage / 5) * 5 - 9;
      this.allSelected = false;
      this.orderIds = [];
    },
    selectPage() {
      this.currentPage = parseInt(event.target.textContent);
      this.allSelected = false;
      this.orderIds = [];
    },
    getReturnCups() {
      const path = `/backend/partner-cup/return`;
      let returnCups = axios.create();

      returnCups
        .get(path)
        .then((res) => {
          this.entryList = res.data;
          this.total = this.entryList.length;
          this.maxPage = Math.ceil(this.total / this.perPage);
        })
        .catch((err) => {
          console.log("returnCups :>> ", err);
        });
    },
    checkAll() {
      this.orderIds = [];

      if (!this.allSelected) {
        for (let index = 1; index <= this.perPage; index++) {
          this.orderIds.push((this.currentPage - 1) * this.perPage + index - 1);
        }
      }
    },
    checkOne() {
      this.allSelected = false;
    },
    submitOrder(index) {
      const returnCup = this.entryList[index];

      console.log("returnCup :>> ", returnCup);

      if (confirm(`이 컵을 회수하시겠습니까?`)) {
        const orderId = returnCup.partnerReturnId;
        const path = `/backend/partner-cup/return`;

        let addOrder = axios.create();

        addOrder
          .post(path, { partnerReturnId: orderId })
          .then(() => {
            this.$emit("makeToast", { status: "success", msg: "회수 했습니다." });
            this.getReturnCups();
          })
          .catch((err) => {
            console.log("err :>> ", err.response);
          });
      }
    },
    submitSelectedOrders() {
      let promises = [];

      if (confirm("모두 회수하시겠습니까?")) {
        for (const orderId of this.orderIds) {
          const cup = this.entryList[orderId];

          promises.push(
            new Promise((resolve) => {
              const path = "/backend/partner-cup/return";

              let submitCups = axios.create();

              submitCups
                .post(path, { partnerOrderId: cup.id })
                .then((res) => {
                  resolve(res.data);
                })
                .catch((err) => {
                  console.log("err :>> ", err);
                });
            }),
          );
        }

        Promise.all(promises).then(() => {
          this.$emit("makeToast", { status: "success", msg: "회수 했습니다." });
          this.getReturnCups();
        });
      }
    },
    changeActiveCategory(category) {
      this.activeCategory = category;
      this.getReturnCups();
    },
  },
  mounted() {
    this.getReturnCups();
  },
};
</script>

<style lang="scss" scoped>
@each $theme in $themes {
  &.#{map-get($theme, "name")} {
    * {
      box-sizing: border-box;
    }

    .return-table-card-container {
      background-color: map-get($map: $theme, $key: "content-background");
      box-shadow: $shadow-light;
      padding: 1rem;
      border-radius: 6px;
      transition: all 0.3s ease-in-out;
      position: relative;
      display: flex;
      flex-direction: column;
      box-sizing: border-box;
      color: map-get($map: $theme, $key: "text-light");
      width: 100%;
      max-width: 1020px;
      margin: 1rem auto;

      .return-table-card {
        display: flex;

        width: 100%;
        margin-right: 1rem;

        .orderlist-wrapper {
          width: 100%;
          display: flex;
          flex-direction: column;

          .orderlist-header {
            display: flex;
            border-bottom: 1px solid map-get($map: $theme, $key: "border");

            .header-nav {
              display: flex;
              align-items: center;

              span {
                color: map-get($map: $theme, $key: "text");
                padding: 1rem 1.2rem;
                user-select: none;
                font-weight: bold;
                font-size: 1.1rem;
              }
            }
          }

          .orderlist-body {
            padding: 1rem;
            background-color: map-get($map: $theme, $key: "background");
            width: 100%;
            height: 100%;

            .orderlist-table {
              width: 100%;
              table-layout: auto;

              .selection-col {
                width: 32px;
              }

              .selection-column {
                cursor: pointer;
              }

              .table-header {
                color: map-get($map: $theme, $key: "text");
                white-space: nowrap;
                user-select: none;

                tr th {
                  padding: 1rem 0;
                  text-align: center;
                  font-size: 0.9rem;
                  font-weight: bold;
                  background-color: map-get($map: $theme, $key: "content-background");

                  &:first-child {
                    border-radius: 6px 0 0 6px;
                  }

                  &:last-child {
                    border-radius: 0 6px 6px 0;
                  }

                  &.selection-column {
                    width: 5%;
                  }
                  &.id {
                    width: 5%;
                  }
                  &.customer {
                    width: 30%;
                  }
                  &.cup {
                    width: 30%;
                  }

                  &.date {
                    width: 20%;
                  }
                  &.action {
                    width: 15%;
                  }
                }
              }

              .table-body {
                padding: 1rem;
                text-align: center;
                font-size: 0.85rem;

                tr:hover {
                  background-color: darken($color: map-get($theme, "background"), $amount: 3%);
                }

                tr td {
                  padding: 1rem 0;
                  cursor: pointer;
                  vertical-align: middle;

                  &:first-child {
                    border-radius: 6px 0 0 6px;
                  }

                  &:last-child {
                    border-radius: 0 6px 6px 0;
                  }

                  &:hover {
                    background-color: darken($color: map-get($theme, "background"), $amount: 6%);
                  }

                  .icon-wrapper {
                    margin: 5px;
                    font-size: 1rem;
                    background-color: rgba(map-get($map: $theme, $key: "text"), 0.1);
                    border-radius: 6px;
                    transition: all 0.2s ease;
                    color: $main-color;
                    padding: 0 0.2rem;

                    &:hover {
                      background-color: $main-color;
                      color: white;
                    }
                  }

                  &.selection-column {
                    width: 5%;
                  }
                  &.id {
                    width: 5%;
                  }
                  &.customer {
                    width: 25%;
                  }
                  &.cup {
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    align-items: center;

                    span {
                      width: fit-content;
                      margin: 5px 0;
                    }
                  }
                }

                &.date {
                  width: 25%;
                }
                &.action {
                  width: 20%;
                }
              }
            }
          }

          .orderlist-footer {
            padding: 0 1rem 1rem 1rem;
            background-color: map-get($map: $theme, $key: "background");
            width: 100%;
            border-radius: 0 0 6px 6px;

            .footer-nav {
              border-top: 1px solid map-get($map: $theme, $key: "border");
              display: flex;
              justify-content: space-between;

              ul {
                display: flex;
                list-style: none;
                box-sizing: border-box;
                align-items: center;
                padding: 1rem 1rem 0 1rem;

                .prev-item {
                  margin-right: 0.35rem;
                  border-radius: 50%;
                }
                .next-item {
                  margin-left: 0.35rem;
                  border-radius: 50%;
                }

                .page-item {
                  width: 25px;
                  height: 25px;
                  display: flex;
                  justify-content: center;
                  align-items: center;
                  border: none;
                  transition: all 0.2s ease-out;
                  cursor: pointer;
                  font-size: 0.9rem;
                  background-color: map-get($map: $theme, $key: "content-background");
                  position: relative;
                  color: map-get($map: $theme, $key: "text-light");
                  outline: none;
                  border-radius: 50%;
                  margin: 0 5px;
                  box-shadow: $shadow-light;
                  transition: all 0.3s ease-in-out;

                  &:hover {
                    background-color: rgba($main-color, 0.6);
                    color: $white;
                    box-shadow: $shadow;
                  }

                  &.active {
                    background-color: $main-color;
                    color: $white;
                  }
                }
              }

              .footer-button-wrapper {
                display: flex;
                align-items: center;
                align-items: center;
                padding: 1rem 1rem 0 1rem;

                .footer-button {
                  margin: 0 5px;
                  width: 25px;
                  height: 25px;
                  border: 1px solid map-get($map: $theme, $key: "border");
                  border-radius: 6px;
                  display: flex;
                  justify-content: center;
                  align-items: center;
                  cursor: pointer;
                  color: $white;
                  transition: all 0.2s ease;

                  &.submit {
                    background-color: $main-color;

                    &:hover,
                    &:active {
                      background-color: map-get($map: $theme, $key: "content-background");
                      border: 3px solid $main-color;
                      color: $main-color;
                      box-shadow: $shadow;
                    }
                  }

                  &.reject {
                    background-color: $error-msg;

                    &:hover,
                    &:active {
                      background-color: map-get($map: $theme, $key: "content-background");
                      border: 3px solid $error-msg;
                      color: $error-msg;
                      box-shadow: $shadow;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>
