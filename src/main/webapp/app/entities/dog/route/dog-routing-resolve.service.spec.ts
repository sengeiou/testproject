jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IDog, Dog } from '../dog.model';
import { DogService } from '../service/dog.service';

import { DogRoutingResolveService } from './dog-routing-resolve.service';

describe('Service Tests', () => {
  describe('Dog routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: DogRoutingResolveService;
    let service: DogService;
    let resultDog: IDog | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(DogRoutingResolveService);
      service = TestBed.inject(DogService);
      resultDog = undefined;
    });

    describe('resolve', () => {
      it('should return IDog returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultDog = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultDog).toEqual({ id: 123 });
      });

      it('should return new IDog if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultDog = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultDog).toEqual(new Dog());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultDog = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultDog).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
