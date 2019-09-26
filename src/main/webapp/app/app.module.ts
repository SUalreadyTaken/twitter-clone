import './vendor.ts';

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap';
import { Ng2Webstorage } from 'ngx-webstorage';
// import { NgJhipsterModule } from 'ng-jhipster';

import { AuthInterceptor } from './blocks/interceptor/auth.interceptor';
import { AuthExpiredInterceptor } from './blocks/interceptor/auth-expired.interceptor';
import { ErrorHandlerInterceptor } from './blocks/interceptor/errorhandler.interceptor';
import { NotificationInterceptor } from './blocks/interceptor/notification.interceptor';
import { TwitterCloneSharedModule } from 'app/shared';
import { TwitterCloneCoreModule } from 'app/core';
import { TwitterCloneAppRoutingModule } from './app-routing.module';
import { TwitterCloneHomeModule } from 'app/home';
import { TwitterCloneAccountModule } from './account/account.module';
import { TwitterCloneEntityModule } from './entities/entity.module';
import * as moment from 'moment';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { JhiMainComponent, NavbarComponent, FooterComponent, PageRibbonComponent, ErrorComponent } from './layouts';
// my imports
// import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ProfilesComponent } from 'app/home/profiles/profiles.component';
import { SearchComponent } from './search/search.component';

@NgModule({
    imports: [
        BrowserModule,
        // BrowserAnimationsModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-' }),
        // NgJhipsterModule.forRoot({
        //     // set below to true to make alerts look like toast
        //     alertAsToast: false,
        //     alertTimeout: 5000
        // }),
        // MatDialogModule,
        // MatFormFieldModule,
        // MatButtonModule,
        // MatInputModule,
        // FormsModule,
        TwitterCloneSharedModule.forRoot(),
        TwitterCloneCoreModule,
        TwitterCloneHomeModule,
        TwitterCloneAccountModule,
        // jhipster-needle-angular-add-module JHipster will add new module here
        TwitterCloneEntityModule,
        TwitterCloneAppRoutingModule
    ],
    declarations: [
        JhiMainComponent,
        NavbarComponent,
        ErrorComponent,
        PageRibbonComponent,
        FooterComponent,
        ProfilesComponent,
        SearchComponent,
    ],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthExpiredInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorHandlerInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: NotificationInterceptor,
            multi: true
        }
    ],
    bootstrap: [JhiMainComponent]
})
export class TwitterCloneAppModule {
    constructor(private dpConfig: NgbDatepickerConfig) {
        this.dpConfig.minDate = { year: moment().year() - 100, month: 1, day: 1 };
    }
}
