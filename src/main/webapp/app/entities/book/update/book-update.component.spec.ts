jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { BookService } from '../service/book.service';
import { IBook, Book } from '../book.model';
import { IAuthor } from 'app/entities/author/author.model';
import { AuthorService } from 'app/entities/author/service/author.service';

import { BookUpdateComponent } from './book-update.component';

describe('Component Tests', () => {
  describe('Book Management Update Component', () => {
    let comp: BookUpdateComponent;
    let fixture: ComponentFixture<BookUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let bookService: BookService;
    let authorService: AuthorService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BookUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(BookUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BookUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      bookService = TestBed.inject(BookService);
      authorService = TestBed.inject(AuthorService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Author query and add missing value', () => {
        const book: IBook = { id: 456 };
        const author: IAuthor = { id: 86583 };
        book.author = author;

        const authorCollection: IAuthor[] = [{ id: 53489 }];
        spyOn(authorService, 'query').and.returnValue(of(new HttpResponse({ body: authorCollection })));
        const additionalAuthors = [author];
        const expectedCollection: IAuthor[] = [...additionalAuthors, ...authorCollection];
        spyOn(authorService, 'addAuthorToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ book });
        comp.ngOnInit();

        expect(authorService.query).toHaveBeenCalled();
        expect(authorService.addAuthorToCollectionIfMissing).toHaveBeenCalledWith(authorCollection, ...additionalAuthors);
        expect(comp.authorsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const book: IBook = { id: 456 };
        const author: IAuthor = { id: 51997 };
        book.author = author;

        activatedRoute.data = of({ book });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(book));
        expect(comp.authorsSharedCollection).toContain(author);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const book = { id: 123 };
        spyOn(bookService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ book });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: book }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(bookService.update).toHaveBeenCalledWith(book);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const book = new Book();
        spyOn(bookService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ book });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: book }));
        saveSubject.complete();

        // THEN
        expect(bookService.create).toHaveBeenCalledWith(book);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const book = { id: 123 };
        spyOn(bookService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ book });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(bookService.update).toHaveBeenCalledWith(book);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackAuthorById', () => {
        it('Should return tracked Author primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackAuthorById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
