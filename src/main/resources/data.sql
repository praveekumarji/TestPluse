INSERT IGNORE INTO subscription_plans (id, plan_code, title, subtitle, duration_days, original_amount_in_paise, discounted_amount_in_paise, display_price, display_original_price, discount_percentage, badge, is_recommended)
VALUES
('plan_annual', 'ANNUAL_SUPER_PASS', 'Annual Super Pass', 'Best value for full exam cycle', 210, 9900, 5900, '₹59', '₹99', 40, 'BEST VALUE', true);

INSERT IGNORE INTO subscription_plan_features (plan_id, feature)
VALUES
('plan_annual', 'Unlimited Full-Length Mock Tests'),
('plan_annual', 'AI Weak Area Deep Analytics'),
('plan_annual', 'Step-by-Step LaTeX Solutions'),
('plan_annual', 'Custom Test Generator Access'),
('plan_annual', 'AI curreted Question Bank'),
('plan_annual', 'VVIP question bank access'),
('plan_annual', 'Previous Year Question Papers with AI based analysis'),
('plan_annual', 'AI based performance analysis');


INSERT IGNORE INTO coupons (code, title, discount_type, discount_value, minimum_amount, valid_from, valid_to, is_active, max_uses, applicable_plan_id, created_at)
VALUES
('WELCOME05', 'Welcome Offer', 'FIXED', 5, 0, NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), true, 1000, null, NOW()),
('TOPPER10', 'Welcome Offer', 'PERCENTAGE', 50, 25000, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), true, 500, 'plan_annual', NOW())
