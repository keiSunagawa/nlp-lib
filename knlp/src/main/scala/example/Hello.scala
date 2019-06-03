package example

object Hello extends Greeting with App {
  (new NLP).run("The small red car turned very quickly around the corner.")
}

trait Greeting {
  lazy val greeting: String = "hello"
}

class NLP {
  import edu.stanford.nlp.ling.CoreAnnotations
  import edu.stanford.nlp.pipeline.Annotation
  import edu.stanford.nlp.pipeline.StanfordCoreNLP
  import edu.stanford.nlp.trees._
  import java.util.Properties
  import collection.JavaConverters._

  val props = new Properties()
  props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse")
  // use faster shift reduce parser
  props.setProperty("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz")
//   props.setProperty("parse.model", "edu/stanford/nlp/models/parser/nndep/english_SD.gz")
  props.setProperty("parse.maxlen", "100")

  val  pipeline = new StanfordCoreNLP(props)

  def run(str: String): Unit =  {
    val annotation = new Annotation(str)
    pipeline.annotate(annotation)
    val tree = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])
      .get(0)
      .get(classOf[TreeCoreAnnotations.TreeAnnotation])
    println(tree)
    val treeConstituents: Set[Constituent] = tree.constituents(new LabeledScoredConstituentFactory()).asScala.toSet
    for {
      constituent <- treeConstituents
    } {
      val label = constituent.label()
      if(label != null && label.toString() == "VP" || label.toString() == "NP") {
        println("found constituent: "+constituent.toString)
        println(tree.getLeaves().subList(constituent.start(), constituent.end()+1))
      }
    }
  }
}
