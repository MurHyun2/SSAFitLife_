import DietView from '@/components/mainBody/DietView.vue';
import PostDetail from "@/components/mainBody/post/PostDetail.vue";
import PostList from "@/components/mainBody/post/PostList.vue";
import PostRegist from "@/components/mainBody/post/PostRegist.vue";
import PostUpdate from "@/components/mainBody/post/PostUpdate.vue";
import PostView from "@/components/mainBody/PostView.vue";
import ProductView from "@/components/mainBody/ProductView.vue";
import WeightView from '@/components/mainBody/WeightView.vue';
import FrontView from '@/views/FrontView.vue';
import MainView from '@/views/MainView.vue';
import {createRouter, createWebHistory} from 'vue-router';
import ActivityManage from "@/components/mainBody/weight/ActivityManage.vue";
import WeightManage from "@/components/mainBody/weight/WeightManage.vue";
import Sup from "@/components/mainBody/product/Sup.vue";
import ChickenBreast from "@/components/mainBody/product/ChickenBreast.vue";
import DietPlan from "@/components/mainBody/diet/DietPlan.vue";

const routes = [
    {
        path: '/',
        name: 'front',
        component: FrontView,
    },
    {
        path: '/',
        name: 'menus',
        component: MainView,
        children: [
            {
                path: 'weight-manage',
                component: WeightView,
                children: [
                    {path: 'weight-manage', component: WeightManage},
                    {path: 'body-record', component: WeightView},
                    {path: 'activity-manage', component: ActivityManage},
                ]
            },
            {
                path: 'diet-manage',
                component: DietView,
                children: [
                    {path: 'diet-plan', component: DietPlan},
                    {path: 'millprep-assist', component: DietView},
                    {path: 'sup-use', component: DietView},
                ]
            },
            {
                path: 'product-recommend',
                component: ProductView,
                children: [
                    {path: 'sup', component: Sup},
                    {path: 'chicken-breast', component: ChickenBreast},
                ]
            },
            {
                path: 'board',
                component: PostView,
                children: [
                    {path: 'posts', name: 'posts', component: PostList},
                    {path: 'posts/regist', name: 'postRegist', component: PostRegist},
                    {path: 'posts/:postNo', name: 'postDetail', component: PostDetail, props: true},
                    {path: 'posts/update/:postNo', name: 'postUpdate', component: PostUpdate, props: true}
                ]
            },
        ]
    }
];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});

router.beforeEach((to, from, next) => {
    const accessToken = localStorage.getItem('accessToken')

    if (!accessToken && to.path !== '/') {
        alert('로그인이 필요한 서비스입니다.')
        next('/')
    } else {
        next()
    }
})
export default router;
