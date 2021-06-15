import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EchoComponent } from '../list/echo.component';
import { EchoDetailComponent } from '../detail/echo-detail.component';
import { EchoUpdateComponent } from '../update/echo-update.component';
import { EchoRoutingResolveService } from './echo-routing-resolve.service';

const echoRoute: Routes = [
  {
    path: '',
    component: EchoComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EchoDetailComponent,
    resolve: {
      echo: EchoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EchoUpdateComponent,
    resolve: {
      echo: EchoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EchoUpdateComponent,
    resolve: {
      echo: EchoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(echoRoute)],
  exports: [RouterModule],
})
export class EchoRoutingModule {}
