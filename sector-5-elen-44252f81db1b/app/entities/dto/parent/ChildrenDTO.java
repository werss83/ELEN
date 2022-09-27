package entities.dto.parent;

import form.parent.ChildrenForm;
import models.children.ChildrenModel;
import models.parent.ParentModel;
import play.data.Form;
import play.data.FormFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ChildrenDTO.
 *
 * @author Pierre Adam
 * @since 20.07.18
 */
public class ChildrenDTO {

    /**
     * The Children list.
     */
    private final List<ChildDTO> data;

    /**
     * The Create form.
     */
    private final Form<ChildrenForm> createForm;

    /**
     * The Open modal.
     */
    private String openModalId;

    /**
     * Instantiates a new Childs dto.
     *
     * @param formFactory the form factory
     * @param parent      the parent
     */
    public ChildrenDTO(final FormFactory formFactory, final ParentModel parent) {
        this.data = new ArrayList<>();
        for (final ChildrenModel child : parent.getChildren()) {
            this.data.add(new ChildDTO(child, formFactory.form(ChildrenForm.class).fill(new ChildrenForm(child))));
        }
        this.createForm = formFactory.form(ChildrenForm.class).fill(new ChildrenForm());
        this.openModalId = null;
    }

    /**
     * Instantiates a new Childs dto.
     *
     * @param formFactory the form factory
     * @param parent      the parent
     * @param errorForm   the error form
     */
    public ChildrenDTO(final FormFactory formFactory, final ParentModel parent, final Form<ChildrenForm> errorForm) {
        this.data = new ArrayList<>();
        final Optional<String> uidFieldValue = errorForm.field("uid").value();
        if (uidFieldValue.isPresent()) {
            final String uid = uidFieldValue.get();
            for (final ChildrenModel child : parent.getChildren()) {
                if (child.getUid().toString().equals(uid)) {
                    final ChildDTO dto = new ChildDTO(child, errorForm);
                    this.data.add(dto);
                    this.openModalId = dto.getEditModalId();
                } else {
                    this.data.add(new ChildDTO(child, formFactory.form(ChildrenForm.class).fill(new ChildrenForm(child))));
                }
            }
            this.createForm = formFactory.form(ChildrenForm.class);
        } else {
            for (final ChildrenModel child : parent.getChildren()) {
                this.data.add(new ChildDTO(child, formFactory.form(ChildrenForm.class).fill(new ChildrenForm(child))));
            }
            this.createForm = errorForm;
            this.openModalId = this.getCreateModalId();
        }
    }

    /**
     * Gets children list.
     *
     * @return the children list
     */
    public List<ChildDTO> getData() {
        return this.data;
    }

    /**
     * Gets create form.
     *
     * @return the create form
     */
    public Form<ChildrenForm> getCreateForm() {
        return this.createForm;
    }

    /**
     * Gets create modal id.
     *
     * @return the create modal id
     */
    public String getCreateModalId() {
        return "create-children";
    }

    /**
     * Gets open modal.
     *
     * @return the open modal
     */
    public String getOpenModalId() {
        return this.openModalId;
    }

    /**
     * The type Child dto.
     */
    public static class ChildDTO {
        /**
         * The Model.
         */
        private final ChildrenModel model;

        /**
         * The Form.
         */
        private final Form<ChildrenForm> form;

        /**
         * The Modal id.
         */
        private final String editModalId;

        /**
         * The Delete modal id.
         */
        private final String deleteModalId;

        /**
         * Instantiates a new Child dto.
         *
         * @param model the model
         * @param form  the form
         */
        public ChildDTO(final ChildrenModel model, final Form<ChildrenForm> form) {
            this.model = model;
            this.form = form;
            this.editModalId = String.format("edit-children-%s", model.getUid().toString());
            this.deleteModalId = String.format("delete-children-%s", model.getUid().toString());
        }

        /**
         * Gets model.
         *
         * @return the model
         */
        public ChildrenModel getModel() {
            return this.model;
        }

        /**
         * Gets form.
         *
         * @return the form
         */
        public Form<ChildrenForm> getForm() {
            return this.form;
        }

        /**
         * Gets modal id.
         *
         * @return the modal id
         */
        public String getEditModalId() {
            return this.editModalId;
        }

        /**
         * Gets delete modal id.
         *
         * @return the delete modal id
         */
        public String getDeleteModalId() {
            return this.deleteModalId;
        }
    }
}
