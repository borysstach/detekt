package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class TooManyFunctions(config: Config = Config.empty, threshold: Int = 10) : ThresholdRule(config, threshold) {

	override val issue = Issue("TooManyFunctions", Severity.Maintainability, "")

	private var amount: Int = 0

	override fun visitKtFile(file: KtFile) {
		super.visitFile(file)
		if (amount > threshold) {
			report(ThresholdedCodeSmell(issue, Entity.from(file), Metric("SIZE", amount, threshold)))
		}
		amount = 0
	}

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		classOrObject.getBody()?.declarations
				?.filterIsInstance<KtNamedFunction>()
				?.sumBy { 1 }
				?.let {
					if (it > threshold) {
						report(ThresholdedCodeSmell(issue, Entity.from(classOrObject), Metric("SIZE", it, threshold)))
					}
				}
		super.visitClassOrObject(classOrObject)
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.isTopLevel) {
			amount++
		}
	}

}