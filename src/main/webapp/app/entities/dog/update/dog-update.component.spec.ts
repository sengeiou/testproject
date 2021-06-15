jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { DogService } from '../service/dog.service';
import { IDog, Dog } from '../dog.model';
import { IActor } from 'app/entities/actor/actor.model';
import { ActorService } from 'app/entities/actor/service/actor.service';

import { DogUpdateComponent } from './dog-update.component';

describe('Component Tests', () => {
  describe('Dog Management Update Component', () => {
    let comp: DogUpdateComponent;
    let fixture: ComponentFixture<DogUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let dogService: DogService;
    let actorService: ActorService;

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
      actorService = TestBed.inject(ActorService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Actor query and add missing value', () => {
        const dog: IDog = { id: 456 };
        const actor: IActor = { id: 89601 };
        dog.actor = actor;

        const actorCollection: IActor[] = [{ id: 15324 }];
        spyOn(actorService, 'query').and.returnValue(of(new HttpResponse({ body: actorCollection })));
        const additionalActors = [actor];
        const expectedCollection: IActor[] = [...additionalActors, ...actorCollection];
        spyOn(actorService, 'addActorToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ dog });
        comp.ngOnInit();

        expect(actorService.query).toHaveBeenCalled();
        expect(actorService.addActorToCollectionIfMissing).toHaveBeenCalledWith(actorCollection, ...additionalActors);
        expect(comp.actorsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const dog: IDog = { id: 456 };
        const actor: IActor = { id: 83996 };
        dog.actor = actor;

        activatedRoute.data = of({ dog });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(dog));
        expect(comp.actorsSharedCollection).toContain(actor);
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

    describe('Tracking relationships identifiers', () => {
      describe('trackActorById', () => {
        it('Should return tracked Actor primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackActorById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
