import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TwitterCloneSharedModule } from 'app/shared';
import { HOME_ROUTE, HomeComponent } from './';

import { MatDialogModule, MatFormFieldModule, MatButtonModule, MatInputModule } from '@angular/material';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BrowserModule } from '@angular/platform-browser';
import { TweetService } from 'app/core/tweet/tweet.service';
import { TimelineComponent } from 'app/home/timeline/timeline.component';
import { MyProfileComponent } from './my-profile/my-profile.component';
import { ProfileService } from 'app/core/profile/profile.service';
import { FollowComponent } from './follow/follow.component';

@NgModule({
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        TwitterCloneSharedModule,
        RouterModule.forChild([HOME_ROUTE]),
        MatDialogModule,
        MatFormFieldModule,
        MatButtonModule,
        MatInputModule,
        FormsModule,
    ],
    declarations: [HomeComponent, TimelineComponent, MyProfileComponent, FollowComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    entryComponents: [],
    providers: [TweetService, ProfileService]
})
export class TwitterCloneHomeModule {}
