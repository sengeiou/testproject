import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IActor, Actor } from '../actor.model';
import { ActorService } from '../service/actor.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { IEcho } from 'app/entities/echo/echo.model';
import { EchoService } from 'app/entities/echo/service/echo.service';

@Component({
  selector: 'jhi-actor-update',
  templateUrl: './actor-update.component.html',
})
export class ActorUpdateComponent implements OnInit {
  isSaving = false;

  booksCollection: IBook[] = [];
  echoesSharedCollection: IEcho[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    firstName: [],
    lastName: [],
    type: [],
    book: [],
    echoes: [],
  });

  constructor(
    protected actorService: ActorService,
    protected bookService: BookService,
    protected echoService: EchoService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ actor }) => {
      this.updateForm(actor);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const actor = this.createFromForm();
    if (actor.id !== undefined) {
      this.subscribeToSaveResponse(this.actorService.update(actor));
    } else {
      this.subscribeToSaveResponse(this.actorService.create(actor));
    }
  }

  trackBookById(index: number, item: IBook): number {
    return item.id!;
  }

  trackEchoById(index: number, item: IEcho): number {
    return item.id!;
  }

  getSelectedEcho(option: IEcho, selectedVals?: IEcho[]): IEcho {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IActor>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(actor: IActor): void {
    this.editForm.patchValue({
      id: actor.id,
      name: actor.name,
      firstName: actor.firstName,
      lastName: actor.lastName,
      type: actor.type,
      book: actor.book,
      echoes: actor.echoes,
    });

    this.booksCollection = this.bookService.addBookToCollectionIfMissing(this.booksCollection, actor.book);
    this.echoesSharedCollection = this.echoService.addEchoToCollectionIfMissing(this.echoesSharedCollection, ...(actor.echoes ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.bookService
      .query({ filter: 'actor-is-null' })
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing(books, this.editForm.get('book')!.value)))
      .subscribe((books: IBook[]) => (this.booksCollection = books));

    this.echoService
      .query()
      .pipe(map((res: HttpResponse<IEcho[]>) => res.body ?? []))
      .pipe(map((echoes: IEcho[]) => this.echoService.addEchoToCollectionIfMissing(echoes, ...(this.editForm.get('echoes')!.value ?? []))))
      .subscribe((echoes: IEcho[]) => (this.echoesSharedCollection = echoes));
  }

  protected createFromForm(): IActor {
    return {
      ...new Actor(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      type: this.editForm.get(['type'])!.value,
      book: this.editForm.get(['book'])!.value,
      echoes: this.editForm.get(['echoes'])!.value,
    };
  }
}
