import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { DogComponent } from './list/dog.component';
import { DogDetailComponent } from './detail/dog-detail.component';
import { DogUpdateComponent } from './update/dog-update.component';
import { DogDeleteDialogComponent } from './delete/dog-delete-dialog.component';
import { DogRoutingModule } from './route/dog-routing.module';

@NgModule({
  imports: [SharedModule, DogRoutingModule],
  declarations: [DogComponent, DogDetailComponent, DogUpdateComponent, DogDeleteDialogComponent],
  entryComponents: [DogDeleteDialogComponent],
})
export class DogModule {}
