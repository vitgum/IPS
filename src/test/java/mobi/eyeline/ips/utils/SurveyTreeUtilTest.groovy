package mobi.eyeline.ips.utils

import mobi.eyeline.ips.components.tree.TreeNode
import mobi.eyeline.ips.model.Question
import mobi.eyeline.ips.model.Survey
import mobi.eyeline.ips.util.SurveyTreeUtil

import static SurveyBuilder.survey
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace

@Mixin(TreeBuilder)
class SurveyTreeUtilTest extends GroovyTestCase {

  void setUp() {
    super.setUp()
    //noinspection UnnecessaryQualifiedReference
    TreeBuilder.init()
  }

  void test1() {

    def survey = survey([:]) {
      pages {
        question(id: 0) {
          option(id: 0, nextPage: ref(id: 1))
          option(id: 1, nextPage: ref(id: 1))
        }
        question(id: 1, defaultPage: ref(id: 1)) {
          option(id: 0, nextPage: ref(id: 2))
          option(id: 1, nextPage: ref(id: 2))
        }
        question(id: 2) {
          option(id: 0, nextPage: ref(id: 3))
          option(id: 1, nextPage: ref(id: 3))
        }
        question(id: 3) {
          option(id: 0, nextPage: null)
          option(id: 1, nextPage: null)
        }
      }
    }


    assertThat asTree(survey).describe(), equalToIgnoringWhiteSpace('''
            Root: [0]

            [0] --0--> [1]
            [0] --1--> [1]

            [1] --0--> [2]
            [1] --1--> [2]

            [2] --0--> [3]
            [2] --1--> [3]

            [3] --0--> [-1]
            [3] --1--> [-1]
            ''')
  }

  void test2() {

    def survey = survey([:]) {
      pages {
        question(id: 0, defaultPage: ref(id: 1)) {
          option(id: 0, nextPage: ref(id: 1))
          option(id: 1, nextPage: ref(id: 1))
        }
        question(id: 1) {
          option(id: 0, nextPage: ref(id: 2))
          option(id: 1, nextPage: ref(id: 2))
        }
        question(id: 2) {
          option(id: 0, nextPage: ref(id: 3))
          option(id: 1, nextPage: ref(id: 3))
        }
        question(id: 3) {
          option(id: 0, nextPage: null)
          option(id: 1, nextPage: null)
        }
      }
    }

    survey.questions.each { Question q -> q.enabledDefaultAnswer = true }

    assertThat asTree(survey).describe(), equalToIgnoringWhiteSpace('''
            Root: [0]

            [0] --default--> [1]
            [0] --0--> [1]
            [0] --1--> [1]

            [1] --default--> [-1]
            [1] --0--> [2]
            [1] --1--> [2]

            [2] --default--> [-1]
            [2] --0--> [3]
            [2] --1--> [3]

            [3] --default--> [-1]
            [3] --0--> [-1]
            [3] --1--> [-1]

            ''')
  }

  void testLoop() {
    def survey = survey([:]) {
      pages {
        question(id: 0) {
          option(id: 0, nextPage: ref(id: 1))
          option(id: 1, nextPage: ref(title: 'Q1'))
          option(id: 2, nextPage: null)
          option(id: 3, nextPage: ref(id: 0))
        }

        question(id: 1, title: 'Q1') {
          option(id: 0, nextPage: ref(id: 0))
          option(id: 1, nextPage: ref(id: 1))
          option(id: 2, nextPage: null)
        }
      }
    }

    assertThat asTree(survey).describe(), equalToIgnoringWhiteSpace('''
            Root: [0]

            [0] --0--> [1]
            [0] --1--> [1]
            [0] --2--> [-1]
            [0] --3--> [0]

            [1] --0--> [0]
            [1] --1--> [1]
            [1] --2--> [-1]
            ''')
  }

  void testExtLink() {
    def survey = survey([:]) {
      pages {
        question(id: 0) {
          option(id: 0, nextPage: ref(id: 1))
        }
        extLink(id: 1)
      }
    }

    assertThat asTree(survey).describe(), equalToIgnoringWhiteSpace('''
            Root: [0]

            [0] --0--> [1]
            ''')
  }

  TreeNode asTree(Survey survey) { SurveyTreeUtil.asTree(survey, '', '', '', '', '', '', '') }
}
