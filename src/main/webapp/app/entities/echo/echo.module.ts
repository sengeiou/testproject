import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { EchoComponent } from './list/echo.component';
import { EchoDetailComponent } from './detail/echo-detail.component';
import { EchoUpdateComponent } from './update/echo-update.component';
import { EchoDeleteDialogComponent } from './delete/echo-delete-dialog.component';
import { EchoRoutingModule } from './route/echo-routing.module';

@NgModule({
  imports: [SharedModule, EchoRoutingModule],
  declarations: [EchoComponent, EchoDetailComponent, EchoUpdateComponent, EchoDeleteDialogComponent],
  entryComponents: [EchoDeleteDialogComponent],
})
export class EchoModule {}
