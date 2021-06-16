jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { DogService } from '../service/dog.service';
import { IDog, Dog } from '../dog.model';

import { DogUpdateComponent } from './dog-update.component';

describe('Component Tests', () => {
  describe('Dog Management Update Component', () => {
    let comp: DogUpdateComponent;
    let fixture: ComponentFixture<DogUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let dogService: DogService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [DogUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(DogUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(DogUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      dogService = TestBed.inject(DogService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const dog: IDog = { id: 456 };

        activatedRoute.data = of({ dog });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(dog));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const dog = { id: 123 };
        spyOn(dogService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ dog });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: dog }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(dogService.update).toHaveBeenCalledWith(dog);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const dog = new Dog();
        spyOn(dogService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ dog });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: dog }));
        saveSubject.complete();

        // THEN
        expect(dogService.create).toHaveBeenCalledWith(dog);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const dog = { id: 123 };
        spyOn(dogService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ dog });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(dogService.update).toHaveBeenCalledWith(dog);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
