import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICard, Card } from '../card.model';
import { CardService } from '../service/card.service';

@Injectable({ providedIn: 'root' })
export class CardRoutingResolveService implements Resolve<ICard> {
  constructor(protected service: CardService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICard> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((card: HttpResponse<Card>) => {
          if (card.body) {
            return of(card.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Card());
  }
}
