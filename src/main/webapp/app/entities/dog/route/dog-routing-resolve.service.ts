import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDog, Dog } from '../dog.model';
import { DogService } from '../service/dog.service';

@Injectable({ providedIn: 'root' })
export class DogRoutingResolveService implements Resolve<IDog> {
  constructor(protected service: DogService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDog> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((dog: HttpResponse<Dog>) => {
          if (dog.body) {
            return of(dog.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Dog());
  }
}
