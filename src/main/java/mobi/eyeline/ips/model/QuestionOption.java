package mobi.eyeline.ips.model;

import com.google.common.base.Predicate;
import mobi.eyeline.ips.util.ListUtils;
import mobi.eyeline.ips.validation.MaxSize;
import org.hibernate.annotations.Cache;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE;

/**
 * Вариант ответа на вопрос; для вопросов, предполагающих варианты ответов.
 */
@Entity
@Table(name = "question_options")
@Proxy(lazy = false)
@Cache(usage = READ_WRITE)
public class QuestionOption implements Serializable {

  /**
   * Идентификатор ответа.
   */
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * Текст ответа.
   */
  @Column(name = "answer", columnDefinition = "TEXT", nullable = false)
  @NotEmpty(message = "{question.option.answer.empty}")
  @MaxSize(70)
  private String answer;

  /**
   * Порядок отображения вопроса в опросе.
   */
  @Column(name = "option_order")
  private int order;

  @JoinColumn(name = "next_question")
  @OneToOne(optional = true)
  private Question nextQuestion = null;

  @ManyToOne(optional = false)
  @JoinColumn(name = "question_id")
  private Question question;

  /**
   * При удалении вариант ответа помечается флагом {@code active = false} в БД и
   * перестает отображаться в веб-интерфейсе.
   */
  @Column(name = "active", columnDefinition = "BIT")
  @Type(type = "org.hibernate.type.NumericBooleanType")
  private boolean active = true;

  public QuestionOption() {
  }

  void prepareIndex() {
    if (getQuestion() != null) {
      order = getQuestion().getOptions().indexOf(this);
    }
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public int getOrder() {
    return order;
  }

  public int getActiveIndex() {
    return getQuestion().getActiveOptions().indexOf(this);
  }

  public Question getQuestion() {
    return question;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void moveTo(int idx) {
    ListUtils.moveTo(getQuestion().getOptions(), this, idx, SKIP_INACTIVE);
  }

  public Question getNextQuestion() {
    return nextQuestion;
  }

  public void setNextQuestion(Question nextQuestion) {
    this.nextQuestion = nextQuestion;
  }

  @Override
  public String toString() {
    return "QuestionOption{" +
        "id=" + id +
        ", answer='" + answer + '\'' +
        '}';
  }


  //
  //
  //

  public static final Predicate<QuestionOption> SKIP_INACTIVE = new Predicate<QuestionOption>() {
    @Override
    public boolean apply(QuestionOption option) {
      return !option.isActive();
    }
  };

}
