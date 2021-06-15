import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CardComponent } from '../list/card.component';
import { CardDetailComponent } from '../detail/card-detail.component';
import { CardUpdateComponent } from '../update/card-update.component';
import { CardRoutingResolveService } from './card-routing-resolve.service';

const cardRoute: Routes = [
  {
    path: '',
    component: CardComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CardDetailComponent,
    resolve: {
      card: CardRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CardUpdateComponent,
    resolve: {
      card: CardRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CardUpdateComponent,
    resolve: {
      card: CardRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(cardRoute)],
  exports: [RouterModule],
})
export class CardRoutingModule {}
