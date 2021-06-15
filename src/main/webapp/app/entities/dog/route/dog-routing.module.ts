import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DogComponent } from '../list/dog.component';
import { DogDetailComponent } from '../detail/dog-detail.component';
import { DogUpdateComponent } from '../update/dog-update.component';
import { DogRoutingResolveService } from './dog-routing-resolve.service';

const dogRoute: Routes = [
  {
    path: '',
    component: DogComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DogDetailComponent,
    resolve: {
      dog: DogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DogUpdateComponent,
    resolve: {
      dog: DogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DogUpdateComponent,
    resolve: {
      dog: DogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(dogRoute)],
  exports: [RouterModule],
})
export class DogRoutingModule {}
