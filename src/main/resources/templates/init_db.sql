TRUNCATE products, producers RESTART IDENTITY CASCADE;

INSERT INTO producers (name)
SELECT 'Producer ' || i FROM generate_series(1, 100) AS i;

INSERT INTO products (name, producer_id, attributes)
SELECT
    'Product ' || s.p_id || ' for ' || prod.name,
    prod.id,
    attr_gen.generated_json
FROM generate_series(1, 10) AS s(p_id)
         CROSS JOIN producers prod
         CROSS JOIN LATERAL (
    SELECT jsonb_object_agg(key, value) AS generated_json
    FROM (
             SELECT * FROM (VALUES
                                ('color', to_jsonb((ARRAY['red', 'blue', 'green', 'black', 'white'])[floor(random()*5+1)::int])),
                                ('material', to_jsonb((ARRAY['steel', 'plastic', 'carbon', 'wood', 'glass'])[floor(random()*5+1)::int])),
                                ('energy_class', to_jsonb((ARRAY['A', 'B', 'C', 'D'])[floor(random()*4+1)::int])),
                                ('voltage', to_jsonb((ARRAY['110V', '230V', '12V', '24V'])[floor(random()*4+1)::int])),
                                ('made_in', to_jsonb((ARRAY['Poland', 'Germany', 'USA', 'China', 'Japan'])[floor(random()*5+1)::int])),
                                ('certification', to_jsonb((ARRAY['CE', 'TUV', 'ISO9001', 'UL'])[floor(random()*4+1)::int])),
                                ('display_type', to_jsonb((ARRAY['OLED', 'LCD', 'IPS'])[floor(random()*3+1)::int])),
                                ('weight', to_jsonb(round((random()*50)::numeric, 2))),
                                ('width', to_jsonb(floor(random()*500)::int)),
                                ('warranty_years', to_jsonb(floor(random()*10)::int)),
                                ('battery_mah', to_jsonb((floor(random()*4000)+1000)::int)),
                                ('eco_score', to_jsonb(floor(random()*100)::int)),
                                ('is_waterproof', to_jsonb(random() > 0.5)),
                                ('is_recycled', to_jsonb(random() > 0.7)),
                                ('wifi_support', to_jsonb(random() > 0.4))
                           ) AS pool(key, value)
             WHERE s.p_id IS NOT NULL
             ORDER BY random()
             LIMIT (floor(random() * 10 + 5))::int
         ) AS random_subset
    ) AS attr_gen;