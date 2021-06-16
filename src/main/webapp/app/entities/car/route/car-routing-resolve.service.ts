import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICar, Car } from '../car.model';
import { CarService } from '../service/car.service';

@Injectable({ providedIn: 'root' })
export class CarRoutingResolveService implements Resolve<ICar> {
  constructor(protected service: CarService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICar> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((car: HttpResponse<Car>) => {
          if (car.body) {
            return of(car.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Car());
  }
}