jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { EchoService } from '../service/echo.service';
import { IEcho, Echo } from '../echo.model';

import { EchoUpdateComponent } from './echo-update.component';

describe('Component Tests', () => {
  describe('Echo Management Update Component', () => {
    let comp: EchoUpdateComponent;
    let fixture: ComponentFixture<EchoUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let echoService: EchoService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [EchoUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(EchoUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(EchoUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      echoService = TestBed.inject(EchoService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const echo: IEcho = { id: 456 };

        activatedRoute.data = of({ echo });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(echo));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const echo = { id: 123 };
        spyOn(echoService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ echo });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: echo }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(echoService.update).toHaveBeenCalledWith(echo);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const echo = new Echo();
        spyOn(echoService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ echo });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: echo }));
        saveSubject.complete();

        // THEN
        expect(echoService.create).toHaveBeenCalledWith(echo);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const echo = { id: 123 };
        spyOn(echoService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ echo });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(echoService.update).toHaveBeenCalledWith(echo);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
