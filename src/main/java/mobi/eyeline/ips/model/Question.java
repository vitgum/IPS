package mobi.eyeline.ips.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "questions")
@Proxy(lazy = false)
public class Question implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    /**
     * Текст вопроса, отображается для респондентов.
     */
    @Column(name = "title", nullable = false)
    @NotEmpty
    private String title;

    /**
     * Тип вопроса.
     */
    @Column(name = "type", columnDefinition = "CHAR", nullable = false)
    @Type(type = "mobi.eyeline.ips.model.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "mobi.eyeline.ips.model.QuestionKind"),
            @Parameter(name = "identifierMethod", value = "getShortName"),
            @Parameter(name = "valueOfMethod", value = "fromShortName")
    })
    private QuestionKind kind;

    /**
     * Варианты ответа на вопрос (если тип вопроса подразумевает их наличие).
     */
    @OneToMany(mappedBy = "question", cascade = ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<QuestionOption> options = new LinkedList<>();

    /**
     * Порядок отображения вопроса в опросе.
     */
    @Column(name = "question_order")
    private int order;

    @PrePersist
    @PreUpdate
    private void prepareIndex() {
        if (getSurvey() != null) {
            order = getSurvey().getQuestions().indexOf(this);
        }
    }

    public Question() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public QuestionKind getKind() {
        return kind;
    }

    public void setKind(QuestionKind kind) {
        this.kind = kind;
    }

    public List<QuestionOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOption> options) {
        this.options = options;
    }

    /**
     * @return Something like {@code QuestionTitle (Option1, Option2, ..)}.
     */
    public String getTitleWithOptions() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getTitle());
        if (getKind() == QuestionKind.ListRadio) {
            builder.append(" (");
            for (int i = 0; i < getOptions().size(); i++) {
                builder.append(getOptions().get(i).getAnswer());
                if (i < options.size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append(")");
        }
        return builder.toString();
    }

    public String getAnswerField() {
        //return LimeSurveyUtils.answerField(getSurvey().getId(), getId(), getGroupId());
        throw new AssertionError("Not implemented yet.");
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        if (survey != null ? !survey.equals(question.survey) : question.survey != null)
            return false;
        if (title != null ? !title.equals(question.title) : question.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = survey != null ? survey.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
