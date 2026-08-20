INSERT IGNORE INTO subscription_plans (id, plan_code, title, subtitle, duration_days, original_amount_in_paise, discounted_amount_in_paise, display_price, display_original_price, discount_percentage, badge, is_recommended)
VALUES
('plan_annual', 'ANNUAL_SUPER_PASS', 'Annual Super Pass', 'Best value for full exam cycle', 365, 99900, 49900, '₹499', '₹999', 50, 'BEST VALUE', true),
('plan_quarterly', 'QUARTERLY_SUPER_PASS', 'Quarterly Super Pass', 'For consistent revision and tests', 90, 49900, 24900, '₹249', '₹499', 50, 'POPULAR', false),
('plan_monthly', 'MONTHLY_SUPER_PASS', 'Monthly Super Pass', 'Quick access for short-term prep', 30, 19900, 9900, '₹99', '₹199', 50, 'NEW', false);

INSERT IGNORE INTO subscription_plan_features (plan_id, feature)
VALUES
('plan_annual', 'Unlimited Full-Length Mock Tests'),
('plan_annual', 'AI Weak Area Deep Analytics'),
('plan_annual', 'Step-by-Step LaTeX Solutions'),
('plan_annual', 'Custom Test Generator Access'),
('plan_quarterly', 'Unlimited Full-Length Mock Tests'),
('plan_quarterly', 'AI Weak Area Deep Analytics'),
('plan_quarterly', 'Performance Tracking'),
('plan_monthly', 'Access to Premium Practice Tests'),
('plan_monthly', 'Limited AI Insights'),
('plan_monthly', 'Basic Analytics');

INSERT IGNORE INTO coupons (code, title, discount_type, discount_value, minimum_amount, valid_from, valid_to, is_active, max_uses, applicable_plan_id, created_at)
VALUES
('WELCOME20', 'Welcome Offer', 'PERCENTAGE', 20, 0, NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), true, 1000, null, NOW()),
('TOPPER50', 'Topper Special', 'PERCENTAGE', 50, 25000, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), true, 500, 'plan_annual', NOW()),
('FLAT499', 'Flat Discount', 'FIXED', 499, 49900, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 200, null, NOW());
