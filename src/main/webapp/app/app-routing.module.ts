import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute, navbarRoute } from './layouts';
import { DEBUG_INFO_ENABLED } from 'app/app.constants';
import { ProfilesComponent } from 'app/home/profiles/profiles.component';
import { SearchComponent } from './search/search.component';
import { FollowComponent } from './home/follow/follow.component';

const LAYOUT_ROUTES = [navbarRoute, ...errorRoute];

@NgModule({
    imports: [
        RouterModule.forRoot(
            [
                { path: 'admin', loadChildren: './admin/admin.module#TwitterCloneAdminModule'},
                { path: 'profiles/:id', component: ProfilesComponent},
                { path: 'search', component: SearchComponent},
                { path: 'profiles/:id/following', component: FollowComponent},
                { path: 'profiles/:id/followers', component: FollowComponent},
                ...LAYOUT_ROUTES
            ],
            { useHash: true, enableTracing: DEBUG_INFO_ENABLED }
        )
    ],
    exports: [RouterModule]
})
export class TwitterCloneAppRoutingModule {}
