import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEcho, Echo } from '../echo.model';
import { EchoService } from '../service/echo.service';

@Injectable({ providedIn: 'root' })
export class EchoRoutingResolveService implements Resolve<IEcho> {
  constructor(protected service: EchoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEcho> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((echo: HttpResponse<Echo>) => {
          if (echo.body) {
            return of(echo.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Echo());
  }
}
