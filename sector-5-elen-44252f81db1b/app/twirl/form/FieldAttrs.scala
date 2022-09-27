package twirl.form

import play.api.data.Field

/**
 * package.
 *
 * @author Pierre Adam
 * @since 20.07.09
 */
class FieldAttrs(field: Field) {

  private val acceptConstraint: Seq[String] = Seq("constraint.required", "constraint.min", "constraint.max", "constraint.minLength", "constraint.maxLength", "constraint.readOnly", "constraint.disabled")
  private var required: Boolean = false
  private var render: Boolean = true
  private var noAutoComplete: Boolean = false

  for (elem <- field.constraints) {
    elem._1 match {
      case "constraint.required" => required = true
      case "constraint.noRender" => render = false
      case "constraint.noAutoComplete" => noAutoComplete = true
      case _ =>
    }
  }

  def isRequired(): Boolean = {
    this.required
  }

  def doRender(): Boolean = {
    this.render
  }

  def getAttrs(extraAttrs: Seq[(String, String)], skipConstraints: Boolean = false): String = {
    var str: String = "";
    if (!skipConstraints) {
      for (elem <- field.constraints) {
        if (acceptConstraint.contains(elem._1)) {
          var value = elem._1.substring(11)
          if (elem._2.nonEmpty) {
            value = elem._2.head.toString
          }
          str += elem._1.substring(11) + "=\"" + value + "\" "
        }
      }
    }
    if (noAutoComplete) {
      str += "autocomplete=\"off\" ";
    }
    for ((key, value) <- extraAttrs) {
      str += "%s=\"%s\" ".format(key, value)
    }
    str
  }
}
