import { NgModule } from '@angular/core';

import { TwitterCloneSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [TwitterCloneSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [TwitterCloneSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class TwitterCloneSharedCommonModule {}
